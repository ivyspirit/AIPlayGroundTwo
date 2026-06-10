package com.example.aiplaygroundtwo.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aiplaygroundtwo.data.network.fake.FakeAgentNetworkApi

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FailureModeDebugDialog(
    onModeSelected: (FakeAgentNetworkApi.FailureMode) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text("Arm next network failure") },
        text = {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FakeAgentNetworkApi.FailureMode.entries
                    .filter { it != FakeAgentNetworkApi.FailureMode.NONE }
                    .forEach { mode ->
                        FilterChip(
                            selected = false,
                            onClick = { onModeSelected(mode) },
                            label = {
                                Text(
                                    text = mode.name.replace('_', ' '),
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            },
                        )
                    }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
