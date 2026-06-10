package com.example.aiplaygroundtwo.ui.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.aiplaygroundtwo.navigation.AppDestinations
import com.example.aiplaygroundtwo.ui.theme.AIPlayGroundTwoTheme

@Composable
fun AgentBottomNav(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier,
) {
    val dashboardSelected = currentRoute == AppDestinations.DASHBOARD ||
        currentRoute?.startsWith("job_detail/") == true
    val requestsSelected = currentRoute == AppDestinations.REQUESTS_CENTER

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


@Composable
private fun AgentBottomNavPreview() {
    AIPlayGroundTwoTheme {
        AgentBottomNav(
            navController = rememberNavController(),
            currentRoute = AppDestinations.DASHBOARD,
        )
    }
}
