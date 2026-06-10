package com.example.aiplaygroundtwo.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.aiplaygroundtwo.domain.model.JobStatus
import com.example.aiplaygroundtwo.domain.model.JobSummary
import com.example.aiplaygroundtwo.ui.theme.AIPlayGroundTwoTheme

@Preview
@Composable
private fun DashboardScreenLoadingPreview() {
    AIPlayGroundTwoTheme {
        DashboardScreen(
            uiState = DashboardUiState.Loading,
            onJobClick = {},
            onRequestsClick = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun DashboardScreenContentPreview() {
    AIPlayGroundTwoTheme {
        DashboardScreen(
            uiState = DashboardUiState.Content(
                jobs = listOf(
                    JobSummary(
                        id = "job-1",
                        title = "Migrate auth to OAuth2",
                        repoName = "my-app-backend",
                        status = JobStatus.Blocked,
                        currentStep = 3,
                        totalSteps = 5,
                        agentCount = 3,
                        pendingApprovalCount = 1,
                        pendingNeedsInputCount = 1,
                    ),
                    JobSummary(
                        id = "job-2",
                        title = "Fix checkout bug",
                        repoName = "shop-app",
                        status = JobStatus.Running,
                        currentStep = 2,
                        totalSteps = 4,
                        agentCount = 1,
                        pendingApprovalCount = 0,
                        pendingNeedsInputCount = 0,
                    ),
                ),
                pendingRequestCount = 2,
            ),
            onJobClick = {},
            onRequestsClick = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun DashboardScreenErrorPreview() {
    AIPlayGroundTwoTheme {
        DashboardScreen(
            uiState = DashboardUiState.Error("Could not load jobs."),
            onJobClick = {},
            onRequestsClick = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun DashboardScreenEmptyPreview() {
    AIPlayGroundTwoTheme {
        DashboardScreen(
            uiState = DashboardUiState.Empty,
            onJobClick = {},
            onRequestsClick = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}
