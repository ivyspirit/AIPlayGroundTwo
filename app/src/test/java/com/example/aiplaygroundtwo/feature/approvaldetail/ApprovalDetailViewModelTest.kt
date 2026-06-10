package com.example.aiplaygroundtwo.feature.approvaldetail

import com.example.aiplaygroundtwo.data.network.NetworkResult
import com.example.aiplaygroundtwo.domain.model.JobStatus
import com.example.aiplaygroundtwo.domain.model.JobSummary
import com.example.aiplaygroundtwo.domain.model.ResolutionAction
import com.example.aiplaygroundtwo.domain.model.RequestStatus
import com.example.aiplaygroundtwo.domain.model.RequestType
import com.example.aiplaygroundtwo.domain.model.ReviewRequest
import com.example.aiplaygroundtwo.domain.model.RiskLevel
import com.example.aiplaygroundtwo.feature.dashboard.FakeAgentRepository
import com.example.aiplaygroundtwo.testutil.MainDispatcherRule
import com.example.aiplaygroundtwo.testutil.TestDispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ApprovalDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val approvalRequest = ReviewRequest(
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

    private val needsInputRequest = ReviewRequest(
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

    private val jobSummary = JobSummary(
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

    private fun createViewModel(
        repository: FakeAgentRepository,
        requestId: String,
    ): ApprovalDetailViewModel = ApprovalDetailViewModel(
        repository = repository,
        requestId = requestId,
        dispatchers = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
    )

    @Test
    fun approve_callsRepositoryWithApprovalAction() = runTest {
        val repository = FakeAgentRepository()
        repository.setJobs(listOf(jobSummary))
        repository.setRequestDetail("request-coder-approval", approvalRequest)
        val viewModel = createViewModel(repository, "request-coder-approval")
        backgroundScope.launch {
            viewModel.uiState.collect { }
            viewModel.shouldNavigateBack.collect { }
        }
        advanceUntilIdle()

        viewModel.approve("Looks good")
        advanceUntilIdle()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val resolution = repository.lastSubmittedResolution
        assertEquals(ResolutionAction.Approve, resolution?.action)
        assertEquals("Looks good", resolution?.feedback)
        assertTrue(viewModel.shouldNavigateBack.value)
    }

    @Test
    fun reject_callsRepositoryWithRejectAction() = runTest {
        val repository = FakeAgentRepository()
        repository.setJobs(listOf(jobSummary))
        repository.setRequestDetail("request-coder-approval", approvalRequest)
        val viewModel = createViewModel(repository, "request-coder-approval")
        backgroundScope.launch {
            viewModel.uiState.collect { }
            viewModel.shouldNavigateBack.collect { }
        }
        advanceUntilIdle()

        viewModel.reject("Too risky")
        advanceUntilIdle()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val resolution = repository.lastSubmittedResolution
        assertEquals(ResolutionAction.Reject, resolution?.action)
        assertEquals("Too risky", resolution?.feedback)
        assertTrue(viewModel.shouldNavigateBack.value)
    }

    @Test
    fun continueWithSelection_callsRepositoryWithOption() = runTest {
        val repository = FakeAgentRepository()
        repository.setJobs(listOf(jobSummary))
        repository.setRequestDetail("request-test-input", needsInputRequest)
        val viewModel = createViewModel(repository, "request-test-input")
        backgroundScope.launch {
            viewModel.uiState.collect { }
            viewModel.shouldNavigateBack.collect { }
        }
        advanceUntilIdle()

        viewModel.continueWithSelection("Smoke only", "Run fast")
        advanceUntilIdle()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val resolution = repository.lastSubmittedResolution
        assertEquals(ResolutionAction.Continue, resolution?.action)
        assertEquals("Smoke only", resolution?.selectedOption)
        assertEquals("Run fast", resolution?.feedback)
        assertTrue(viewModel.shouldNavigateBack.value)
    }

    @Test
    fun submitFailure_surfacesErrorWithoutNavigateBack() = runTest {
        val repository = FakeAgentRepository()
        repository.setJobs(listOf(jobSummary))
        repository.setRequestDetail("request-coder-approval", approvalRequest)
        repository.submitResult = NetworkResult.HttpError(500, "Server error")
        val viewModel = createViewModel(repository, "request-coder-approval")
        backgroundScope.launch {
            viewModel.uiState.collect { }
            viewModel.shouldNavigateBack.collect { }
        }
        advanceUntilIdle()

        viewModel.approve("feedback")
        advanceUntilIdle()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value as ApprovalDetailUiState.Content
        assertEquals("Server error", state.submitError)
        assertFalse(viewModel.shouldNavigateBack.value)
    }
}
