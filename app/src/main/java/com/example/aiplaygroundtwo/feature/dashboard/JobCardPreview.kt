package com.example.aiplaygroundtwo.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.aiplaygroundtwo.domain.model.JobStatus
import com.example.aiplaygroundtwo.domain.model.JobSummary
import com.example.aiplaygroundtwo.ui.theme.AIPlayGroundTwoTheme

@Preview
@Composable
private fun JobCardBlockedPreview() {
    AIPlayGroundTwoTheme {
        JobCard(
            job = JobSummary(
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
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun JobCardRunningPreview() {
    AIPlayGroundTwoTheme {
        JobCard(
            job = JobSummary(
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
            onClick = {},
        )
    }
}
