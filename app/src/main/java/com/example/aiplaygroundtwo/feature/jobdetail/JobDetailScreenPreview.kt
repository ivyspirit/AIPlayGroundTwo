package com.example.aiplaygroundtwo.feature.jobdetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.aiplaygroundtwo.domain.model.Agent
import com.example.aiplaygroundtwo.domain.model.AgentStatus
import com.example.aiplaygroundtwo.domain.model.ActivityEvent
import com.example.aiplaygroundtwo.domain.model.JobStatus
import com.example.aiplaygroundtwo.domain.model.RequestStatus
import com.example.aiplaygroundtwo.domain.model.RequestType
import com.example.aiplaygroundtwo.domain.model.ReviewRequest
import com.example.aiplaygroundtwo.domain.model.RiskLevel
import com.example.aiplaygroundtwo.ui.theme.AIPlayGroundTwoTheme

@Preview
@Composable
private fun JobDetailScreenLoadingPreview() {
    AIPlayGroundTwoTheme {
        JobDetailScreen(
            uiState = JobDetailUiState.Loading,
            inspectorState = null,
            onBack = {},
            onReview = {},
            onAgentClick = {},
            onDismissInspector = {},
        )
    }
}

@Preview
@Composable
private fun JobDetailScreenContentPreview() {
    val pending = ReviewRequest(
        id = "request-coder-approval",
        jobId = "job-1",
        agentId = "agent-coder",
        agentName = "Coder Agent",
        type = RequestType.Approval,
        status = RequestStatus.Pending,
        title = "Delete legacy/auth/",
        risk = RiskLevel.High,
        reasoning = "Legacy auth module is unused.",
        requestedAtEpochMs = 1_700_000_060_000L,
        proposedAction = null,
        affectedFiles = null,
        question = null,
        options = null,
        selectedOption = null,
        feedback = null,
    )
    AIPlayGroundTwoTheme {
        JobDetailScreen(
            uiState = JobDetailUiState.Content(
                title = "Migrate auth to OAuth2",
                repoName = "my-app-backend",
                status = JobStatus.Blocked,
                currentStep = 3,
                totalSteps = 5,
                startedAtEpochMs = 1_700_000_000_000L,
                updatedAtEpochMs = 1_700_000_180_000L,
                agents = listOf(
                    Agent(
                        id = "agent-coder",
                        jobId = "job-1",
                        name = "Coder Agent",
                        role = "Coder",
                        status = AgentStatus.Blocked,
                        currentSummary = "Waiting on delete approval",
                        pendingRequestId = "request-coder-approval",
                    ),
                ),
                pendingRequests = listOf(pending),
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
            ),
            inspectorState = null,
            onBack = {},
            onReview = {},
            onAgentClick = {},
            onDismissInspector = {},
        )
    }
}
