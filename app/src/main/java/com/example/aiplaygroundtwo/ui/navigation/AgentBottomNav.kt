package com.example.aiplaygroundtwo.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.aiplaygroundtwo.navigation.AppDestinations
import com.example.aiplaygroundtwo.ui.theme.AIPlayGroundTwoTheme
import com.example.aiplaygroundtwo.ui.util.isLandscape

@Composable
fun AgentBottomNav(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier,
) {
    val dashboardSelected = currentRoute == AppDestinations.DASHBOARD ||
        currentRoute?.startsWith("job_detail/") == true
    val requestsSelected = currentRoute == AppDestinations.REQUESTS_CENTER
    val compact = isLandscape()

    if (compact) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(MaterialTheme.colorScheme.surface),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompactNavItem(
                label = "Dashboard",
                icon = "⌂",
                selected = dashboardSelected,
                onClick = {
                    navController.navigate(AppDestinations.DASHBOARD) {
                        popUpTo(AppDestinations.DASHBOARD) { inclusive = false }
                        launchSingleTop = true
                    }
                },
            )
            CompactNavItem(
                label = "Requests",
                icon = "✓",
                selected = requestsSelected,
                onClick = {
                    navController.navigate(AppDestinations.REQUESTS_CENTER) {
                        popUpTo(AppDestinations.DASHBOARD) { inclusive = false }
                        launchSingleTop = true
                    }
                },
            )
        }
    } else {
        NavigationBar(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            NavigationBarItem(
                selected = dashboardSelected,
                onClick = {
                    navController.navigate(AppDestinations.DASHBOARD) {
                        popUpTo(AppDestinations.DASHBOARD) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                icon = { Text("⌂") },
                label = { Text("Dashboard") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
            NavigationBarItem(
                selected = requestsSelected,
                onClick = {
                    navController.navigate(AppDestinations.REQUESTS_CENTER) {
                        popUpTo(AppDestinations.DASHBOARD) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                icon = { Text("✓") },
                label = { Text("Requests") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        }
    }
}

@Composable
private fun CompactNavItem(
    label: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = icon,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
    }
}

@Composable
private fun AgentBottomNavPreview() {
    AIPlayGroundTwoTheme {
        AgentBottomNav(
            navController = rememberNavController(),
            currentRoute = AppDestinations.DASHBOARD,
        )
    }
}
