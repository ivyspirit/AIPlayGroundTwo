package com.example.aiplaygroundtwo.ui.placeholder

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// Minimal back arrow — avoids material-icons-extended dependency.
internal val BackIcon: ImageVector = ImageVector.Builder(
    name = "Back",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply {
    path(fill = SolidColor(Color.White)) {
        moveTo(20f, 11f)
        lineTo(7.83f, 11f)
        lineTo(12.71f, 6.12f)
        lineTo(11.29f, 4.71f)
        lineTo(4f, 12f)
        lineTo(11.29f, 19.29f)
        lineTo(12.71f, 17.88f)
        lineTo(7.83f, 13f)
        lineTo(20f, 13f)
        close()
    }
}.build()
