package com.example.aiplaygroundtwo.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.aiplaygroundtwo.data.repository.AgentRepository
import com.example.aiplaygroundtwo.di.DispatcherProvider
import com.example.aiplaygroundtwo.navigation.AppDestinations
import com.example.aiplaygroundtwo.ui.navigation.AgentBottomNav
import com.example.aiplaygroundtwo.ui.navigation.AgentNavHost

@Composable
fun AgentApp(
    repository: AgentRepository,
    dispatchers: DispatcherProvider,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route
    val showBottomNav = AppDestinations.showsBottomNav(currentRoute)

    LaunchedEffect(repository) {
        repository.seedIfEmpty()
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomNav) {
                AgentBottomNav(
                    navController = navController,
                    currentRoute = currentRoute,
                )
            }
        },
    ) { innerPadding ->
        AgentNavHost(
            navController = navController,
            repository = repository,
            dispatchers = dispatchers,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
