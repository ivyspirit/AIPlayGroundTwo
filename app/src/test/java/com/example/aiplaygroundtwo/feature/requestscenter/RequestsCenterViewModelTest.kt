package com.example.aiplaygroundtwo.feature.requestscenter

import com.example.aiplaygroundtwo.domain.model.PendingJobGroup
import com.example.aiplaygroundtwo.domain.model.RequestStatus
import com.example.aiplaygroundtwo.domain.model.RequestType
import com.example.aiplaygroundtwo.domain.model.RequestsCenter
import com.example.aiplaygroundtwo.domain.model.ReviewRequest
import com.example.aiplaygroundtwo.domain.model.RiskLevel
import com.example.aiplaygroundtwo.feature.dashboard.FakeAgentRepository
import com.example.aiplaygroundtwo.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RequestsCenterViewModelTest {
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
        proposedAction = null,
        affectedFiles = null,
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
        question = null,
        options = null,
        selectedOption = null,
        feedback = null,
    )

    private val historyApproved = ReviewRequest(
        id = "request-history-approved",
        jobId = "job-1",
        agentId = "agent-architect",
        agentName = "Architect Agent",
        type = RequestType.Approval,
        status = RequestStatus.Approved,
        title = "Add OAuth callback route",
        risk = RiskLevel.Medium,
        reasoning = "Callback route required.",
        requestedAtEpochMs = 1_700_000_000_000L,
        proposedAction = null,
        affectedFiles = null,
        question = null,
        options = null,
        selectedOption = null,
        feedback = null,
    )

    @Test
    fun uiState_partitionsPendingAndHistory() = runTest {
        val repository = FakeAgentRepository()
        repository.setRequestsCenter(
            RequestsCenter(
                pendingByJob = listOf(
                    PendingJobGroup(
                        jobId = "job-1",
                        jobTitle = "Migrate auth to OAuth2",
                        repoName = "my-app-backend",
                        pendingCount = 2,
                        requests = listOf(coderApproval, testInput),
                    ),
                ),
                history = listOf(historyApproved),
            ),
        )
        val viewModel = RequestsCenterViewModel(repository)
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        val state = viewModel.uiState.value as RequestsCenterUiState.Content
        assertEquals(2, state.pendingCount)
        assertEquals(1, state.pendingByJob.size)
        assertEquals("Migrate auth to OAuth2", state.pendingByJob.first().jobTitle)
        assertEquals(2, state.pendingByJob.first().requests.size)
        assertEquals(1, state.history.size)
        assertEquals("request-history-approved", state.historyPreview?.id)
    }

    @Test
    fun uiState_groupsPendingRequestsByJob() = runTest {
        val repository = FakeAgentRepository()
        repository.setRequestsCenter(
            RequestsCenter(
                pendingByJob = listOf(
                    PendingJobGroup(
                        jobId = "job-1",
                        jobTitle = "Migrate auth to OAuth2",
                        repoName = "my-app-backend",
                        pendingCount = 2,
                        requests = listOf(coderApproval, testInput),
                    ),
                    PendingJobGroup(
                        jobId = "job-2",
                        jobTitle = "Fix checkout bug",
                        repoName = "shop-app",
                        pendingCount = 1,
                        requests = listOf(
                            coderApproval.copy(
                                id = "request-checkout",
                                jobId = "job-2",
                                title = "Deploy hotfix",
                            ),
                        ),
                    ),
                ),
                history = emptyList(),
            ),
        )
        val viewModel = RequestsCenterViewModel(repository)
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        val state = viewModel.uiState.value as RequestsCenterUiState.Content
        assertEquals(3, state.pendingCount)
        assertEquals(2, state.pendingByJob.size)
        assertEquals("my-app-backend", state.pendingByJob.first().repoName)
        assertEquals("shop-app", state.pendingByJob.last().repoName)
        assertNull(state.historyPreview)
    }
}
