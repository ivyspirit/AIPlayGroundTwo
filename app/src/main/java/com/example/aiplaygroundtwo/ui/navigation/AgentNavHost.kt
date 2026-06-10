package com.example.aiplaygroundtwo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.aiplaygroundtwo.data.repository.AgentRepository
import com.example.aiplaygroundtwo.di.DispatcherProvider
import com.example.aiplaygroundtwo.feature.dashboard.DashboardScreen
import com.example.aiplaygroundtwo.feature.dashboard.DashboardViewModel
import com.example.aiplaygroundtwo.feature.dashboard.DashboardViewModelFactory
import com.example.aiplaygroundtwo.feature.jobdetail.JobDetailScreen
import com.example.aiplaygroundtwo.feature.jobdetail.JobDetailViewModel
import com.example.aiplaygroundtwo.feature.jobdetail.JobDetailViewModelFactory
import com.example.aiplaygroundtwo.navigation.AppDestinations
import com.example.aiplaygroundtwo.ui.placeholder.ApprovalDetailPlaceholderScreen
import com.example.aiplaygroundtwo.ui.placeholder.RequestsCenterPlaceholderScreen

@Composable
fun AgentNavHost(
    navController: NavHostController,
    repository: AgentRepository,
    dispatchers: DispatcherProvider,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.DASHBOARD,
        modifier = modifier,
    ) {
        composable(AppDestinations.DASHBOARD) {
            val viewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModelFactory(repository, dispatchers),
            )
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
            DashboardScreen(
                uiState = uiState,
                onJobClick = { jobId ->
                    navController.navigate(AppDestinations.jobDetail(jobId))
                },
                onRequestsClick = {
                    navController.navigate(AppDestinations.REQUESTS_CENTER)
                },
                onRefresh = viewModel::refresh,
                onRetry = viewModel::refresh,
            )
        }
        composable(
            route = AppDestinations.JOB_DETAIL,
            arguments = listOf(
                navArgument(AppDestinations.JOB_ID_ARG) { type = NavType.StringType },
            ),
        ) { entry ->
            val jobId = entry.arguments?.getString(AppDestinations.JOB_ID_ARG).orEmpty()
            val viewModel: JobDetailViewModel = viewModel(
                factory = JobDetailViewModelFactory(repository, jobId),
            )
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
            val inspectorState = viewModel.inspectorState.collectAsStateWithLifecycle().value
            JobDetailScreen(
                uiState = uiState,
                inspectorState = inspectorState,
                onBack = { navController.popBackStack() },
                onReview = { requestId ->
                    navController.navigate(AppDestinations.approvalDetail(requestId))
                },
                onAgentClick = viewModel::onAgentClick,
                onDismissInspector = viewModel::dismissInspector,
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
