package com.example.aiplaygroundtwo.feature.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aiplaygroundtwo.domain.model.JobStatus
import com.example.aiplaygroundtwo.domain.model.JobSummary
import com.example.aiplaygroundtwo.ui.components.AgentChip
import com.example.aiplaygroundtwo.ui.components.ChipColors
import com.example.aiplaygroundtwo.ui.components.JobStatusChip
import com.example.aiplaygroundtwo.ui.theme.AIPlayGroundTwoTheme
import com.example.aiplaygroundtwo.ui.theme.AgentBlue
import com.example.aiplaygroundtwo.ui.theme.AgentBlueContainer
import com.example.aiplaygroundtwo.ui.theme.AgentRed
import com.example.aiplaygroundtwo.ui.theme.AgentRedContainer

@Composable
fun JobCard(
    job: JobSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                JobStatusChip(status = job.status)
            }
            Text(
                text = "Repo: ${job.repoName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${job.agentCount} agents",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "Step ${job.currentStep} of ${job.totalSteps}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            LinearProgressIndicator(
                progress = { job.currentStep.toFloat() / job.totalSteps.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface,
            )
            if (job.pendingApprovalCount > 0 || job.pendingNeedsInputCount > 0) {
                PendingRequestBreakdown(
                    approvalCount = job.pendingApprovalCount,
                    needsInputCount = job.pendingNeedsInputCount,
                )
            }
        }
    }
}

@Composable
private fun PendingRequestBreakdown(
    approvalCount: Int,
    needsInputCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Pending requests",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (approvalCount > 0) {
                AgentChip(
                    label = "$approvalCount Approval",
                    colors = ChipColors(AgentRedContainer, AgentRed),
                )
            }
            if (needsInputCount > 0) {
                AgentChip(
                    label = "$needsInputCount Needs Input",
                    colors = ChipColors(AgentBlueContainer, AgentBlue),
                )
            }
        }
    }
}

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
