package com.example.aiplaygroundtwo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.aiplaygroundtwo.domain.model.RiskLevel
import com.example.aiplaygroundtwo.ui.theme.AIPlayGroundTwoTheme
import com.example.aiplaygroundtwo.ui.theme.AgentAmber
import com.example.aiplaygroundtwo.ui.theme.AgentAmberContainer
import com.example.aiplaygroundtwo.ui.theme.AgentOnSurfaceMuted
import com.example.aiplaygroundtwo.ui.theme.AgentRed
import com.example.aiplaygroundtwo.ui.theme.AgentRedContainer
import com.example.aiplaygroundtwo.ui.theme.AgentSurfaceVariant

@Composable
fun RiskChip(
    risk: RiskLevel,
    modifier: Modifier = Modifier,
) {
    val (label, colors) = when (risk) {
        RiskLevel.High -> "High" to ChipColors(AgentRedContainer, AgentRed)
        RiskLevel.Medium -> "Medium" to ChipColors(AgentAmberContainer, AgentAmber)
        RiskLevel.Low -> "Low" to ChipColors(AgentSurfaceVariant, AgentOnSurfaceMuted)
    }
    AgentChip(
        label = label,
        colors = colors,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun RiskChipPreview() {
    AIPlayGroundTwoTheme {
        RiskChip(risk = RiskLevel.High)
    }
}
