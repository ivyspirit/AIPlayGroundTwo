package com.example.aiplaygroundtwo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class ChipColors(
    val container: Color,
    val content: Color,
)

@Composable
fun AgentChip(
    label: String,
    colors: ChipColors,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        color = colors.content,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(colors.container)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}
