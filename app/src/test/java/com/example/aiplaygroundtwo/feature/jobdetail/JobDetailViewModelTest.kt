package com.example.aiplaygroundtwo.feature.jobdetail

import com.example.aiplaygroundtwo.domain.model.Agent
import com.example.aiplaygroundtwo.domain.model.AgentStatus
import com.example.aiplaygroundtwo.domain.model.ActivityEvent
import com.example.aiplaygroundtwo.domain.model.JobDetail
import com.example.aiplaygroundtwo.domain.model.JobStatus
import com.example.aiplaygroundtwo.domain.model.RequestStatus
import com.example.aiplaygroundtwo.domain.model.RequestType
import com.example.aiplaygroundtwo.domain.model.ReviewRequest
import com.example.aiplaygroundtwo.domain.model.RiskLevel
import com.example.aiplaygroundtwo.feature.dashboard.FakeAgentRepository
import com.example.aiplaygroundtwo.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JobDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val coderApproval = ReviewRequest(
        id = "request-coder-approval",
        jobId = "job-1",
        agentId = "agent-coder",
        agentName = "Coder Agent",
        type = RequestType.Approval,
        status = RequestStatus.Pending,
        title = "Delete legacy/auth/",
        risk = RiskLevel.High,
        reasoning = "Legacy auth is unused.",
        requestedAtEpochMs = 1_700_000_060_000L,
        proposedAction = "Delete directory",
        affectedFiles = listOf("legacy/auth/"),
        question = null,
        options = null,
        selectedOption = null,
        feedback = null,
    )

    private val testInput = ReviewRequest(
        id = "request-test-input",
        jobId = "job-1",
        agentId = "agent-test",
        agentName = "Test Agent",
        type = RequestType.NeedsInput,
        status = RequestStatus.Pending,
        title = "Choose test scope",
        risk = RiskLevel.Medium,
        reasoning = "Scope affects runtime.",
        requestedAtEpochMs = 1_700_000_120_000L,
        proposedAction = null,
        affectedFiles = null,
        question = "Which scope?",
        options = listOf("Smoke only", "Critical flows", "Full integration"),
        selectedOption = null,
        feedback = null,
    )

    private val jobDetail = JobDetail(
        id = "job-1",
        title = "Migrate auth to OAuth2",
        repoName = "my-app-backend",
        status = JobStatus.Blocked,
        currentStep = 3,
        totalSteps = 5,
        startedAtEpochMs = 1_700_000_000_000L,
        updatedAtEpochMs = 1_700_000_180_000L,
        agents = listOf(
            Agent(
                id = "agent-architect",
                jobId = "job-1",
                name = "Architect Agent",
                role = "Architect",
                status = AgentStatus.Completed,
                currentSummary = "Completed plan",
                pendingRequestId = null,
            ),
            Agent(
                id = "agent-coder",
                jobId = "job-1",
                name = "Coder Agent",
                role = "Coder",
                status = AgentStatus.Blocked,
                currentSummary = "Waiting on delete approval",
                pendingRequestId = "request-coder-approval",
            ),
            Agent(
                id = "agent-test",
                jobId = "job-1",
                name = "Test Agent",
                role = "Test",
                status = AgentStatus.Blocked,
                currentSummary = "Waiting on test scope selection",
                pendingRequestId = "request-test-input",
            ),
        ),
        pendingRequests = listOf(coderApproval, testInput),
        activityEvents = listOf(
            ActivityEvent(
                id = "event-2",
                jobId = "job-1",
                agentId = "agent-coder",
                agentName = "Coder Agent",
                message = "Coder Agent requested delete approval",
                occurredAtEpochMs = 1_700_000_000_000L,
            ),
        ),
    )

    @Test
    fun uiState_mapsHeaderPendingListAndBlockedStatus() = runTest {
        val repository = FakeAgentRepository()
        repository.setJobDetail("job-1", jobDetail)
        val viewModel = JobDetailViewModel(repository, "job-1")
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        val state = viewModel.uiState.value as JobDetailUiState.Content
        assertEquals("my-app-backend", state.repoName)
        assertEquals(JobStatus.Blocked, state.status)
        assertEquals(3, state.currentStep)
        assertEquals(5, state.totalSteps)
        assertEquals(2, state.pendingRequests.size)
        assertEquals("Delete legacy/auth/", state.pendingRequests.first().title)
    }

    @Test
    fun inspector_blockedAgentShowsReview() = runTest {
        val repository = FakeAgentRepository()
        repository.setJobDetail("job-1", jobDetail)
        val viewModel = JobDetailViewModel(repository, "job-1")
        backgroundScope.launch {
            viewModel.uiState.collect { }
            viewModel.inspectorState.collect { }
        }
        advanceUntilIdle()

        viewModel.onAgentClick("agent-coder")
        advanceUntilIdle()

        val inspector = viewModel.inspectorState.value
        assertNotNull(inspector)
        assertTrue(inspector!!.showReview)
        assertEquals("request-coder-approval", inspector.pendingRequest?.id)
        assertEquals("Legacy auth is unused.", inspector.pendingRequest?.reasoning)
        assertEquals(1, inspector.recentActions.size)
    }

    @Test
    fun inspector_idleAgentHidesReview() = runTest {
        val repository = FakeAgentRepository()
        repository.setJobDetail("job-1", jobDetail)
        val viewModel = JobDetailViewModel(repository, "job-1")
        backgroundScope.launch {
            viewModel.uiState.collect { }
            viewModel.inspectorState.collect { }
        }
        advanceUntilIdle()

        viewModel.onAgentClick("agent-architect")
        advanceUntilIdle()

        val inspector = viewModel.inspectorState.value
        assertNotNull(inspector)
        assertFalse(inspector!!.showReview)
        assertNull(inspector.pendingRequest)
    }
}
