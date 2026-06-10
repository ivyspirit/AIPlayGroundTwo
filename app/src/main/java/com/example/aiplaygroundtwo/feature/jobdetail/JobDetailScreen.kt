package com.example.aiplaygroundtwo.feature.jobdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aiplaygroundtwo.ui.components.AgentLoadingState
import com.example.aiplaygroundtwo.ui.placeholder.BackIcon

private enum class JobDetailTab(val label: String) {
    Overview("Overview"),
    Agents("Agents"),
    Activity("Activity"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    uiState: JobDetailUiState,
    inspectorState: InspectorUiState?,
    onBack: () -> Unit,
    onReview: (String) -> Unit,
    onAgentClick: (String) -> Unit,
    onDismissInspector: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        JobDetailUiState.Loading -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    JobDetailTopBar(title = "Job Detail", onBack = onBack)
                },
            ) { innerPadding ->
                AgentLoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }
        }
        is JobDetailUiState.Content -> {
            var selectedTab by rememberSaveable { mutableIntStateOf(0) }
            Scaffold(
                modifier = modifier,
                topBar = {
                    JobDetailTopBar(title = uiState.title, onBack = onBack)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    JobDetailHeader(
                        repoName = uiState.repoName,
                        status = uiState.status,
                        currentStep = uiState.currentStep,
                        totalSteps = uiState.totalSteps,
                        startedAtEpochMs = uiState.startedAtEpochMs,
                        updatedAtEpochMs = uiState.updatedAtEpochMs,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                    TabRow(selectedTabIndex = selectedTab) {
                        JobDetailTab.entries.forEachIndexed { index, tab ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(tab.label) },
                            )
                        }
                    }
                    when (JobDetailTab.entries[selectedTab]) {
                        JobDetailTab.Overview -> JobDetailOverviewTab(
                            pendingRequests = uiState.pendingRequests,
                            agentCount = uiState.agents.size,
                            onReview = onReview,
                            modifier = Modifier.fillMaxSize(),
                        )
                        JobDetailTab.Agents -> JobDetailAgentsTab(
                            agents = uiState.agents,
                            onAgentClick = onAgentClick,
                            modifier = Modifier.fillMaxSize(),
                        )
                        JobDetailTab.Activity -> JobDetailActivityTab(
                            activityEvents = uiState.activityEvents,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
            inspectorState?.let { inspector ->
                AgentInspectorSheet(
                    state = inspector,
                    onDismiss = onDismissInspector,
                    onReview = { requestId ->
                        onDismissInspector()
                        onReview(requestId)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JobDetailTopBar(
    title: String,
    onBack: () -> Unit,
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = BackIcon, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}
