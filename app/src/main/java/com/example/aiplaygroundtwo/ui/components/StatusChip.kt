package com.example.aiplaygroundtwo.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.aiplaygroundtwo.domain.model.AgentStatus
import com.example.aiplaygroundtwo.domain.model.JobStatus
import com.example.aiplaygroundtwo.ui.theme.AIPlayGroundTwoTheme
import com.example.aiplaygroundtwo.ui.theme.AgentBlue
import com.example.aiplaygroundtwo.ui.theme.AgentBlueContainer
import com.example.aiplaygroundtwo.ui.theme.AgentAmber
import com.example.aiplaygroundtwo.ui.theme.AgentAmberContainer
import com.example.aiplaygroundtwo.ui.theme.AgentGreen
import com.example.aiplaygroundtwo.ui.theme.AgentGreenContainer
import com.example.aiplaygroundtwo.ui.theme.AgentRed
import com.example.aiplaygroundtwo.ui.theme.AgentRedContainer

@Composable
fun JobStatusChip(
    status: JobStatus,
    modifier: Modifier = Modifier,
) {
    val (label, colors) = when (status) {
        JobStatus.Blocked -> "Blocked" to ChipColors(AgentRedContainer, AgentRed)
        JobStatus.Running -> "Running" to ChipColors(AgentGreenContainer, AgentGreen)
        JobStatus.Completed -> "Completed" to ChipColors(AgentGreenContainer, AgentGreen)
    }
    AgentChip(
        label = label,
        colors = colors,
        modifier = modifier,
    )
}

@Composable
fun AgentStatusChip(
    status: AgentStatus,
    modifier: Modifier = Modifier,
) {
    val (label, colors) = when (status) {
        AgentStatus.Blocked -> "Blocked" to ChipColors(AgentRedContainer, AgentRed)
        AgentStatus.Running -> "Running" to ChipColors(AgentGreenContainer, AgentGreen)
        AgentStatus.Completed -> "Completed" to ChipColors(AgentGreenContainer, AgentGreen)
        AgentStatus.Idle -> "Idle" to ChipColors(AgentAmberContainer, AgentAmber)
    }
    AgentChip(
        label = label,
        colors = colors,
        modifier = modifier,
    )
}


@Composable
private fun JobStatusChipPreview() {
    AIPlayGroundTwoTheme {
        JobStatusChip(status = JobStatus.Blocked)
    }
}


@Composable
private fun AgentStatusChipPreview() {
    AIPlayGroundTwoTheme {
        AgentStatusChip(status = AgentStatus.Running)
    }
}
