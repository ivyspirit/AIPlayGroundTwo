package com.example.aiplaygroundtwo.feature.jobdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aiplaygroundtwo.ui.components.AgentStatusChip
import com.example.aiplaygroundtwo.ui.components.RiskChip
import com.example.aiplaygroundtwo.ui.components.TypeChip
import com.example.aiplaygroundtwo.ui.util.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AgentInspectorSheet(
    state: InspectorUiState,
    onDismiss: () -> Unit,
    onReview: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = state.agentName, style = MaterialTheme.typography.titleLarge)
                AgentStatusChip(status = state.agentStatus)
            }
            Text(
                text = "Role: ${state.role}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Job: ${state.jobTitle}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Currently: ${state.currentSummary}",
                style = MaterialTheme.typography.bodyMedium,
            )
            state.pendingRequest?.let { request ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TypeChip(type = request.type)
                            RiskChip(risk = request.risk)
                        }
                        Text(
                            text = "Action: ${request.title}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        if (state.showReview) {
                            Button(onClick = { onReview(request.id) }) {
                                Text("Review")
                            }
                        }
                    }
                }
                Text(
                    text = "Why: ${request.reasoning}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (state.recentActions.isNotEmpty()) {
                Text(text = "Recent actions", style = MaterialTheme.typography.titleSmall)
                state.recentActions.forEach { event ->
                    Text(
                        text = "• ${formatTime(event.occurredAtEpochMs)} — ${event.message}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
