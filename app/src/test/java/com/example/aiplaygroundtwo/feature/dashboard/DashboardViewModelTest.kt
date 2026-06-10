package com.example.aiplaygroundtwo.feature.dashboard

import com.example.aiplaygroundtwo.data.network.NetworkResult
import com.example.aiplaygroundtwo.domain.model.JobStatus
import com.example.aiplaygroundtwo.domain.model.JobSummary
import com.example.aiplaygroundtwo.testutil.MainDispatcherRule
import com.example.aiplaygroundtwo.testutil.TestDispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val blockedJob = JobSummary(
        id = "job-1",
        title = "Migrate auth to OAuth2",
        repoName = "my-app-backend",
        status = JobStatus.Blocked,
        currentStep = 3,
        totalSteps = 5,
        agentCount = 3,
        pendingApprovalCount = 1,
        pendingNeedsInputCount = 1,
    )

    private val runningJob = JobSummary(
        id = "job-2",
        title = "Fix checkout bug",
        repoName = "shop-app",
        status = JobStatus.Running,
        currentStep = 2,
        totalSteps = 4,
        agentCount = 1,
        pendingApprovalCount = 0,
        pendingNeedsInputCount = 0,
    )

    @Test
    fun refresh_emitsLoadingThenContent() = runTest {
        val repository = FakeAgentRepository()
        val viewModel = DashboardViewModel(
            repository = repository,
            dispatchers = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
        )
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        repository.setJobs(listOf(blockedJob, runningJob))
        advanceUntilIdle()

        val state = viewModel.uiState.value as DashboardUiState.Content
        assertEquals(2, state.jobs.size)
        assertEquals("Migrate auth to OAuth2", state.jobs.first().title)
    }

    @Test
    fun refreshFailure_emitsErrorWhenNoCachedJobs() = runTest {
        val repository = FakeAgentRepository()
        repository.refreshResult = NetworkResult.NetworkError(
            IllegalStateException("Simulated network failure"),
        )
        val viewModel = DashboardViewModel(
            repository = repository,
            dispatchers = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
        )
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is DashboardUiState.Error)
        assertEquals("Simulated network failure", (state as DashboardUiState.Error).message)
    }

    @Test
    fun content_includesPendingCountsAndBreakdown() = runTest {
        val repository = FakeAgentRepository()
        repository.setJobs(listOf(blockedJob, runningJob))
        val viewModel = DashboardViewModel(
            repository = repository,
            dispatchers = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
        )
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        val state = viewModel.uiState.value as DashboardUiState.Content
        assertEquals(2, state.pendingRequestCount)
        val job1 = state.jobs.first { it.id == "job-1" }
        assertEquals(1, job1.pendingApprovalCount)
        assertEquals(1, job1.pendingNeedsInputCount)
        assertEquals(JobStatus.Blocked, job1.status)
    }
}
