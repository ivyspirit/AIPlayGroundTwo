package com.example.aiplaygroundtwo.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aiplaygroundtwo.ui.placeholder.BackIcon
import com.example.aiplaygroundtwo.ui.util.isLandscape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentScreenTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onTitleLongClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val compact = isLandscape()
    TopAppBar(
        modifier = modifier.then(
            if (compact) Modifier.height(52.dp) else Modifier,
        ),
        windowInsets = WindowInsets.statusBars,
        title = {
            Text(
                text = title,
                modifier = if (onTitleLongClick != null) {
                    Modifier.pointerInput(onTitleLongClick) {
                        detectTapGestures(onLongPress = { onTitleLongClick() })
                    }
                } else {
                    Modifier
                },
                style = if (compact) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.titleLarge
                },
                maxLines = 1,
            )
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = BackIcon, contentDescription = "Back")
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}
