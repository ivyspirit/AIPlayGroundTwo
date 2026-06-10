package com.example.aiplaygroundtwo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.aiplaygroundtwo.domain.model.RequestStatus
import com.example.aiplaygroundtwo.ui.theme.AgentBlue
import com.example.aiplaygroundtwo.ui.theme.AgentBlueContainer
import com.example.aiplaygroundtwo.ui.theme.AgentGreen
import com.example.aiplaygroundtwo.ui.theme.AgentGreenContainer
import com.example.aiplaygroundtwo.ui.theme.AgentRed
import com.example.aiplaygroundtwo.ui.theme.AgentRedContainer

@Composable
fun OutcomeChip(
    status: RequestStatus,
    selectedOption: String?,
    modifier: Modifier = Modifier,
) {
    val (label, colors) = when (status) {
        RequestStatus.Approved -> "Approved" to ChipColors(AgentGreenContainer, AgentGreen)
        RequestStatus.Rejected -> "Rejected" to ChipColors(AgentRedContainer, AgentRed)
        RequestStatus.InputSelected -> {
            val optionLabel = selectedOption?.let { "Input selected · $it" } ?: "Input selected"
            optionLabel to ChipColors(AgentBlueContainer, AgentBlue)
        }
        RequestStatus.Pending -> "Pending" to ChipColors(AgentBlueContainer, AgentBlue)
    }
    AgentChip(
        label = label,
        colors = colors,
        modifier = modifier,
    )
}
