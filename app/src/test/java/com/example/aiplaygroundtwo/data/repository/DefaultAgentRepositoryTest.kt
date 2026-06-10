package com.example.aiplaygroundtwo.data.repository

import androidx.room.Room
import com.example.aiplaygroundtwo.data.local.AgentDatabase
import com.example.aiplaygroundtwo.data.network.fake.FakeAgentNetworkApi
import com.example.aiplaygroundtwo.domain.model.JobStatus
import com.example.aiplaygroundtwo.domain.model.RequestStatus
import com.example.aiplaygroundtwo.domain.model.ResolutionAction
import com.example.aiplaygroundtwo.domain.model.ReviewResolution
import com.example.aiplaygroundtwo.testutil.MainDispatcherRule
import com.example.aiplaygroundtwo.testutil.TestDispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class DefaultAgentRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var database: AgentDatabase
    private lateinit var networkApi: FakeAgentNetworkApi
    private lateinit var repository: DefaultAgentRepository

    @Before
    fun setUp() {
        val context = RuntimeEnvironment.getApplication()
        database = Room.inMemoryDatabaseBuilder(context, AgentDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        networkApi = FakeAgentNetworkApi(simulatedDelayMs = 0L)
        repository = DefaultAgentRepository(
            database = database,
            networkApi = networkApi,
            dispatchers = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun refresh_populatesRoom() = runTest {
        repository.refresh()
        val jobs = repository.observeJobs().first()
        assertEquals(3, jobs.size)
        assertEquals("Migrate auth to OAuth2", jobs.first { it.id == "job-1" }.title)
    }

    @Test
    fun resolution_updatesRequestAgentAndActivity() = runTest {
        repository.refresh()
        repository.submitReviewResolution(
            ReviewResolution(
                requestId = "request-coder-approval",
                action = ResolutionAction.Approve,
            ),
        )

        val request = repository.observeRequestDetail("request-coder-approval").first()
        val jobDetail = repository.observeJobDetail("job-1").first()
        val coder = jobDetail?.agents?.first { it.id == "agent-coder" }

        assertNotNull(request)
        assertEquals(RequestStatus.Approved, request?.status)
        assertEquals(com.example.aiplaygroundtwo.domain.model.AgentStatus.Running, coder?.status)
        assertTrue(jobDetail?.activityEvents?.any { it.message.contains("received approval") } == true)
    }

    @Test
    fun pendingRequest_movesToHistory() = runTest {
        repository.refresh()
        val pendingBefore = repository.observeRequestsCenter().first()
            .pendingByJob.flatMap { it.requests }
        assertTrue(pendingBefore.any { it.id == "request-coder-approval" })

        repository.submitReviewResolution(
            ReviewResolution(
                requestId = "request-coder-approval",
                action = ResolutionAction.Approve,
            ),
        )

        val center = repository.observeRequestsCenter().first()
        val pendingAfter = center.pendingByJob.flatMap { it.requests }
        val history = center.history

        assertTrue(pendingAfter.none { it.id == "request-coder-approval" })
        assertTrue(history.any { it.id == "request-coder-approval" })
    }

    @Test
    fun observeJobs_reflectsResolutionOnDashboard() = runTest {
        repository.refresh()
        repository.submitReviewResolution(
            ReviewResolution(
                requestId = "request-coder-approval",
                action = ResolutionAction.Approve,
            ),
        )

        val job1 = repository.observeJobs().first().first { it.id == "job-1" }
        assertEquals(JobStatus.Blocked, job1.status)
        assertEquals(0, job1.pendingApprovalCount)
        assertEquals(1, job1.pendingNeedsInputCount)

        repository.submitReviewResolution(
            ReviewResolution(
                requestId = "request-test-input",
                action = ResolutionAction.Continue,
                selectedOption = "Critical flows",
            ),
        )

        val job1After = repository.observeJobs().first().first { it.id == "job-1" }
        assertEquals(JobStatus.Running, job1After.status)
        assertEquals(0, job1After.pendingApprovalCount)
        assertEquals(0, job1After.pendingNeedsInputCount)
    }

    @Test
    fun jobStaysBlocked_whenAnotherPendingRequestRemains() = runTest {
        repository.refresh()
        repository.submitReviewResolution(
            ReviewResolution(
                requestId = "request-coder-approval",
                action = ResolutionAction.Approve,
            ),
        )

        val job = repository.observeJobDetail("job-1").first()
        assertNotNull(job)
        assertEquals(JobStatus.Blocked, job?.status)
        assertEquals(1, job?.pendingRequests?.size)
    }
}
