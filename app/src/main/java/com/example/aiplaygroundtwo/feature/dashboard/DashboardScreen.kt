package com.example.aiplaygroundtwo.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aiplaygroundtwo.domain.model.JobSummary
import com.example.aiplaygroundtwo.ui.components.AgentEmptyState
import com.example.aiplaygroundtwo.ui.components.AgentErrorState
import com.example.aiplaygroundtwo.ui.components.AgentLoadingState
import com.example.aiplaygroundtwo.ui.components.AgentScreenTopBar

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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            AgentScreenTopBar(
                title = "Agent Control",
                actions = {
                    Surface(
                        onClick = onRequestsClick,
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Text(
                            text = "Requests (${
                                when (uiState) {
                                    is DashboardUiState.Content -> uiState.pendingRequestCount
                                    else -> 0
                                }
                            })",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                },
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

@Composable
internal fun DashboardContent(
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
