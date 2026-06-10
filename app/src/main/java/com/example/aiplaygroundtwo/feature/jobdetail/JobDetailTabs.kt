package com.example.aiplaygroundtwo.feature.jobdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aiplaygroundtwo.domain.model.Agent
import com.example.aiplaygroundtwo.domain.model.ReviewRequest
import com.example.aiplaygroundtwo.ui.components.AgentStatusChip
import com.example.aiplaygroundtwo.ui.components.RiskChip
import com.example.aiplaygroundtwo.ui.components.TypeChip
import com.example.aiplaygroundtwo.ui.util.formatTime

internal fun LazyListScope.jobDetailOverviewItems(
    pendingRequests: List<ReviewRequest>,
    agentCount: Int,
    onReview: (String) -> Unit,
) {
    item(key = "overview-header") {
        Text(
            text = "Review requests (${pendingRequests.size})",
            style = MaterialTheme.typography.titleMedium,
        )
    }
    items(pendingRequests, key = { it.id }) { request ->
        PendingRequestCard(request = request, onReview = { onReview(request.id) })
    }
    item(key = "overview-footer") {
        Text(
            text = "$agentCount agents on this job · ${pendingRequests.size} pending requests",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

internal fun LazyListScope.jobDetailAgentsItems(
    agents: List<Agent>,
    onAgentClick: (String) -> Unit,
) {
    items(agents, key = { it.id }) { agent ->
        AgentRow(agent = agent, onClick = { onAgentClick(agent.id) })
    }
}

internal fun LazyListScope.jobDetailActivityItems(
    activityEvents: List<com.example.aiplaygroundtwo.domain.model.ActivityEvent>,
) {
    items(activityEvents, key = { it.id }) { event ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "●",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
            Column {
                Text(
                    text = formatTime(event.occurredAtEpochMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = event.message,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun PendingRequestCard(
    request: ReviewRequest,
    onReview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = request.agentName,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                )
                TypeChip(type = request.type)
            }
            Text(
                text = "Action: ${request.title}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RiskChip(risk = request.risk)
                Button(onClick = onReview) {
                    Text("Review")
                }
            }
        }
    }
}

@Composable
private fun AgentRow(
    agent: Agent,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = agent.name, style = MaterialTheme.typography.titleSmall)
                    AgentStatusChip(status = agent.status)
                }
                Text(
                    text = agent.currentSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Text(
                text = "›",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
