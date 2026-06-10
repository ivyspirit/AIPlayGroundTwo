package com.example.aiplaygroundtwo.feature.approvaldetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.aiplaygroundtwo.domain.model.RequestStatus
import com.example.aiplaygroundtwo.domain.model.RequestType
import com.example.aiplaygroundtwo.domain.model.ReviewRequest
import com.example.aiplaygroundtwo.domain.model.RiskLevel
import com.example.aiplaygroundtwo.ui.theme.AIPlayGroundTwoTheme

private val approvalRequest = ReviewRequest(
    id = "request-coder-approval",
    jobId = "job-1",
    agentId = "agent-coder",
    agentName = "Coder Agent",
    type = RequestType.Approval,
    status = RequestStatus.Pending,
    title = "Delete legacy/auth/",
    risk = RiskLevel.High,
    reasoning = "Legacy auth module is unused after OAuth scaffolding.",
    requestedAtEpochMs = 1_700_000_060_000L,
    proposedAction = "Delete directory legacy/auth/ and remove imports",
    affectedFiles = listOf(
        "legacy/auth/session.ts",
        "legacy/auth/login.ts",
        "legacy/auth/index.ts",
    ),
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
    reasoning = "Test scope affects runtime and coverage before merge.",
    requestedAtEpochMs = 1_700_000_120_000L,
    proposedAction = null,
    affectedFiles = null,
    question = "Which test scope should run before merge?",
    options = listOf("Smoke only", "Critical flows", "Full integration"),
    selectedOption = null,
    feedback = null,
)

@Preview
@Composable
private fun ApprovalDetailScreenLoadingPreview() {
    AIPlayGroundTwoTheme {
        ApprovalDetailScreen(
            uiState = ApprovalDetailUiState.Loading,
            onBack = {},
            onApprove = {},
            onReject = {},
            onContinue = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun ApprovalDetailScreenApprovalPreview() {
    AIPlayGroundTwoTheme {
        ApprovalDetailScreen(
            uiState = ApprovalDetailUiState.Content(
                request = approvalRequest,
                jobTitle = "Migrate auth to OAuth2",
                repoName = "my-app-backend",
                isSubmitting = false,
                submitError = null,
                isAlreadyResolved = false,
            ),
            onBack = {},
            onApprove = {},
            onReject = {},
            onContinue = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun ApprovalDetailScreenNeedsInputPreview() {
    AIPlayGroundTwoTheme {
        ApprovalDetailScreen(
            uiState = ApprovalDetailUiState.Content(
                request = needsInputRequest,
                jobTitle = "Migrate auth to OAuth2",
                repoName = "my-app-backend",
                isSubmitting = false,
                submitError = null,
                isAlreadyResolved = false,
            ),
            onBack = {},
            onApprove = {},
            onReject = {},
            onContinue = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun ApprovalDetailScreenAlreadyResolvedPreview() {
    AIPlayGroundTwoTheme {
        ApprovalDetailScreen(
            uiState = ApprovalDetailUiState.Content(
                request = approvalRequest.copy(status = RequestStatus.Approved),
                jobTitle = "Migrate auth to OAuth2",
                repoName = "my-app-backend",
                isSubmitting = false,
                submitError = null,
                isAlreadyResolved = true,
            ),
            onBack = {},
            onApprove = {},
            onReject = {},
            onContinue = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun ApprovalDetailScreenErrorPreview() {
    AIPlayGroundTwoTheme {
        ApprovalDetailScreen(
            uiState = ApprovalDetailUiState.Error(message = "Request not found"),
            onBack = {},
            onApprove = {},
            onReject = {},
            onContinue = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun ApprovalDetailScreenSubmitErrorPreview() {
    AIPlayGroundTwoTheme {
        ApprovalDetailScreen(
            uiState = ApprovalDetailUiState.Content(
                request = approvalRequest,
                jobTitle = "Migrate auth to OAuth2",
                repoName = "my-app-backend",
                isSubmitting = false,
                submitError = "Network error",
                isAlreadyResolved = false,
            ),
            onBack = {},
            onApprove = {},
            onReject = {},
            onContinue = { _, _ -> },
        )
    }
}
