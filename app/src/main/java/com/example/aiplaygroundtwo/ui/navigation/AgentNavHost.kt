package com.example.aiplaygroundtwo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.aiplaygroundtwo.navigation.AppDestinations
import com.example.aiplaygroundtwo.ui.placeholder.ApprovalDetailPlaceholderScreen
import com.example.aiplaygroundtwo.ui.placeholder.DashboardPlaceholderScreen
import com.example.aiplaygroundtwo.ui.placeholder.JobDetailPlaceholderScreen
import com.example.aiplaygroundtwo.ui.placeholder.RequestsCenterPlaceholderScreen

@Composable
fun AgentNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.DASHBOARD,
        modifier = modifier,
    ) {
        composable(AppDestinations.DASHBOARD) {
            DashboardPlaceholderScreen(
                onOpenJob = { jobId ->
                    navController.navigate(AppDestinations.jobDetail(jobId))
                },
            )
        }
        composable(
            route = AppDestinations.JOB_DETAIL,
            arguments = listOf(
                navArgument(AppDestinations.JOB_ID_ARG) { type = NavType.StringType },
            ),
        ) { entry ->
            val jobId = entry.arguments?.getString(AppDestinations.JOB_ID_ARG).orEmpty()
            JobDetailPlaceholderScreen(
                jobId = jobId,
                onBack = { navController.popBackStack() },
                onOpenApproval = { requestId ->
                    navController.navigate(AppDestinations.approvalDetail(requestId))
                },
            )
        }
        composable(AppDestinations.REQUESTS_CENTER) {
            RequestsCenterPlaceholderScreen(
                onBack = {
                    navController.navigate(AppDestinations.DASHBOARD) {
                        popUpTo(AppDestinations.DASHBOARD) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onOpenApproval = { requestId ->
                    navController.navigate(AppDestinations.approvalDetail(requestId))
                },
            )
        }
        composable(
            route = AppDestinations.APPROVAL_DETAIL,
            arguments = listOf(
                navArgument(AppDestinations.REQUEST_ID_ARG) { type = NavType.StringType },
            ),
        ) { entry ->
            val requestId = entry.arguments?.getString(AppDestinations.REQUEST_ID_ARG).orEmpty()
            ApprovalDetailPlaceholderScreen(
                requestId = requestId,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
