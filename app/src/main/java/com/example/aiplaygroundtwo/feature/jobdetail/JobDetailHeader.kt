package com.example.aiplaygroundtwo.feature.jobdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aiplaygroundtwo.domain.model.JobStatus
import com.example.aiplaygroundtwo.ui.components.AgentProgressBar
import com.example.aiplaygroundtwo.ui.components.JobStatusChip
import com.example.aiplaygroundtwo.ui.util.formatTime

@Composable
internal fun JobDetailHeader(
    repoName: String,
    status: JobStatus,
    currentStep: Int,
    totalSteps: Int,
    startedAtEpochMs: Long,
    updatedAtEpochMs: Long,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    val padding = if (compact) 10.dp else 16.dp
    val spacing = if (compact) 8.dp else 12.dp
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
    ) {
        if (compact) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MetadataCell(label = "REPO", value = repoName, modifier = Modifier.weight(1f))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "STATUS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    JobStatusChip(status = status, modifier = Modifier.padding(top = 2.dp))
                }
                Column(modifier = Modifier.weight(1.2f)) {
                    Text(
                        text = "PROGRESS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Step $currentStep of $totalSteps",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                    AgentProgressBar(
                        currentStep = currentStep,
                        totalSteps = totalSteps,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                MetadataCell(
                    label = "STARTED",
                    value = formatTime(startedAtEpochMs),
                    modifier = Modifier.weight(0.8f),
                )
                MetadataCell(
                    label = "UPDATED",
                    value = formatTime(updatedAtEpochMs),
                    modifier = Modifier.weight(0.8f),
                )
            }
        } else {
            Column(
                modifier = Modifier.padding(padding),
                verticalArrangement = Arrangement.spacedBy(spacing),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    MetadataCell(label = "REPO", value = repoName, modifier = Modifier.weight(1f))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "STATUS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        JobStatusChip(status = status, modifier = Modifier.padding(top = 4.dp))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PROGRESS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "Step $currentStep of $totalSteps",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                        AgentProgressBar(
                            currentStep = currentStep,
                            totalSteps = totalSteps,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    MetadataCell(
                        label = "STARTED",
                        value = formatTime(startedAtEpochMs),
                        modifier = Modifier.weight(1f),
                    )
                }
                MetadataCell(label = "UPDATED", value = formatTime(updatedAtEpochMs))
            }
        }
    }
}

@Composable
private fun MetadataCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
