package com.example.aiplaygroundtwo.feature.requestscenter

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.aiplaygroundtwo.domain.model.PendingJobGroup
import com.example.aiplaygroundtwo.domain.model.RequestStatus
import com.example.aiplaygroundtwo.domain.model.RequestType
import com.example.aiplaygroundtwo.domain.model.ReviewRequest
import com.example.aiplaygroundtwo.domain.model.RiskLevel
import com.example.aiplaygroundtwo.ui.theme.AIPlayGroundTwoTheme

private val previewCoderApproval = ReviewRequest(
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

private val previewTestInput = ReviewRequest(
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

private val previewHistory = ReviewRequest(
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

@Preview
@Composable
private fun RequestsCenterScreenLoadingPreview() {
    AIPlayGroundTwoTheme {
        RequestsCenterScreen(
            uiState = RequestsCenterUiState.Loading,
            onBack = {},
            onReview = {},
        )
    }
}

@Preview
@Composable
private fun RequestsCenterScreenPendingPreview() {
    AIPlayGroundTwoTheme {
        RequestsCenterScreen(
            uiState = RequestsCenterUiState.Content(
                pendingCount = 2,
                pendingByJob = listOf(
                    PendingJobGroup(
                        jobId = "job-1",
                        jobTitle = "Migrate auth to OAuth2",
                        repoName = "my-app-backend",
                        pendingCount = 2,
                        requests = listOf(previewCoderApproval, previewTestInput),
                    ),
                ),
                history = listOf(previewHistory),
                historyPreview = previewHistory,
            ),
            onBack = {},
            onReview = {},
        )
    }
}

@Preview
@Composable
private fun RequestsCenterScreenEmptyPendingPreview() {
    AIPlayGroundTwoTheme {
        RequestsCenterScreen(
            uiState = RequestsCenterUiState.Content(
                pendingCount = 0,
                pendingByJob = emptyList(),
                history = listOf(previewHistory),
                historyPreview = previewHistory,
            ),
            onBack = {},
            onReview = {},
        )
    }
}

@Preview
@Composable
private fun RequestsCenterScreenEmptyHistoryPreview() {
    AIPlayGroundTwoTheme {
        RequestsCenterScreen(
            uiState = RequestsCenterUiState.Content(
                pendingCount = 2,
                pendingByJob = listOf(
                    PendingJobGroup(
                        jobId = "job-1",
                        jobTitle = "Migrate auth to OAuth2",
                        repoName = "my-app-backend",
                        pendingCount = 2,
                        requests = listOf(previewCoderApproval, previewTestInput),
                    ),
                ),
                history = emptyList(),
                historyPreview = null,
            ),
            onBack = {},
            onReview = {},
        )
    }
}
