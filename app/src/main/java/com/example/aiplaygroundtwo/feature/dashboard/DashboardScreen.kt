package com.example.aiplaygroundtwo.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aiplaygroundtwo.domain.model.JobStatus
import com.example.aiplaygroundtwo.domain.model.JobSummary
import com.example.aiplaygroundtwo.ui.components.AgentEmptyState
import com.example.aiplaygroundtwo.ui.components.AgentErrorState
import com.example.aiplaygroundtwo.ui.components.AgentLoadingState
import com.example.aiplaygroundtwo.ui.theme.AIPlayGroundTwoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onJobClick: (String) -> Unit,
    onRequestsClick: () -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            DashboardTopBar(
                pendingRequestCount = when (uiState) {
                    is DashboardUiState.Content -> uiState.pendingRequestCount
                    else -> 0
                },
                onRequestsClick = onRequestsClick,
            )
        },
    ) { innerPadding ->
        when (uiState) {
            DashboardUiState.Loading -> AgentLoadingState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
            is DashboardUiState.Error -> AgentErrorState(
                message = uiState.message,
                onRetry = onRetry,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
            DashboardUiState.Empty -> AgentEmptyState(
                message = "No active jobs",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
            is DashboardUiState.Content -> PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                DashboardContent(
                    jobs = uiState.jobs,
                    onJobClick = onJobClick,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(
    pendingRequestCount: Int,
    onRequestsClick: () -> Unit,
) {
    TopAppBar(
        title = { Text("Agent Control") },
        actions = {
            Surface(
                onClick = onRequestsClick,
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Text(
                    text = "Requests ($pendingRequestCount)",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

@Composable
private fun DashboardContent(
    jobs: List<JobSummary>,
    onJobClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "Jobs",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        items(jobs, key = { it.id }) { job ->
            JobCard(
                job = job,
                onClick = { onJobClick(job.id) },
            )
        }
    }
}

@Preview
@Composable
private fun DashboardScreenLoadingPreview() {
    AIPlayGroundTwoTheme {
        DashboardScreen(
            uiState = DashboardUiState.Loading,
            onJobClick = {},
            onRequestsClick = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun DashboardScreenContentPreview() {
    AIPlayGroundTwoTheme {
        DashboardScreen(
            uiState = DashboardUiState.Content(
                jobs = listOf(
                    JobSummary(
                        id = "job-1",
                        title = "Migrate auth to OAuth2",
                        repoName = "my-app-backend",
                        status = JobStatus.Blocked,
                        currentStep = 3,
                        totalSteps = 5,
                        agentCount = 3,
                        pendingApprovalCount = 1,
                        pendingNeedsInputCount = 1,
                    ),
                    JobSummary(
                        id = "job-2",
                        title = "Fix checkout bug",
                        repoName = "shop-app",
                        status = JobStatus.Running,
                        currentStep = 2,
                        totalSteps = 4,
                        agentCount = 1,
                        pendingApprovalCount = 0,
                        pendingNeedsInputCount = 0,
                    ),
                ),
                pendingRequestCount = 2,
            ),
            onJobClick = {},
            onRequestsClick = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun DashboardScreenErrorPreview() {
    AIPlayGroundTwoTheme {
        DashboardScreen(
            uiState = DashboardUiState.Error("Could not load jobs."),
            onJobClick = {},
            onRequestsClick = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun DashboardScreenEmptyPreview() {
    AIPlayGroundTwoTheme {
        DashboardScreen(
            uiState = DashboardUiState.Empty,
            onJobClick = {},
            onRequestsClick = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}
