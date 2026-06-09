package com.example.aiplaygroundtwo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AgentDarkColorScheme = darkColorScheme(
    primary = AgentPurple,
    onPrimary = Color.White,
    primaryContainer = AgentPurpleDark,
    onPrimaryContainer = Color.White,
    secondary = AgentBlue,
    onSecondary = Color.White,
    background = AgentBackground,
    onBackground = AgentOnSurface,
    surface = AgentSurface,
    onSurface = AgentOnSurface,
    surfaceVariant = AgentSurfaceVariant,
    onSurfaceVariant = AgentOnSurfaceMuted,
    error = AgentRed,
    onError = Color.White,
)

@Composable
fun AIPlayGroundTwoTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = AgentDarkColorScheme,
        typography = Typography,
        content = content,
    )
}
