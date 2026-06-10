package com.example.aiplaygroundtwo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.aiplaygroundtwo.domain.model.RequestType
import com.example.aiplaygroundtwo.ui.theme.AIPlayGroundTwoTheme
import com.example.aiplaygroundtwo.ui.theme.AgentBlue
import com.example.aiplaygroundtwo.ui.theme.AgentBlueContainer
import com.example.aiplaygroundtwo.ui.theme.AgentRed
import com.example.aiplaygroundtwo.ui.theme.AgentRedContainer

@Composable
fun TypeChip(
    type: RequestType,
    modifier: Modifier = Modifier,
) {
    val (label, colors) = when (type) {
        RequestType.Approval -> "Approval" to ChipColors(AgentRedContainer, AgentRed)
        RequestType.NeedsInput -> "Needs Input" to ChipColors(AgentBlueContainer, AgentBlue)
    }
    AgentChip(
        label = label,
        colors = colors,
        modifier = modifier,
    )
}


@Composable
private fun TypeChipPreview() {
    AIPlayGroundTwoTheme {
        TypeChip(type = RequestType.Approval)
    }
}
