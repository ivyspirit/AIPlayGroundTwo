package com.example.aiplaygroundtwo.feature.jobdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aiplaygroundtwo.ui.components.AgentLoadingState
import com.example.aiplaygroundtwo.ui.components.AgentScreenTopBar
import com.example.aiplaygroundtwo.ui.util.isLandscape

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
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    AgentScreenTopBar(title = "Job Detail", onBack = onBack)
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
            val compact = isLandscape()
            Scaffold(
                modifier = modifier,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    AgentScreenTopBar(title = uiState.title, onBack = onBack)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    TabRow(selectedTabIndex = selectedTab) {
                        JobDetailTab.entries.forEachIndexed { index, tab ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = tab.label,
                                        style = if (compact) {
                                            MaterialTheme.typography.labelLarge
                                        } else {
                                            MaterialTheme.typography.titleSmall
                                        },
                                    )
                                },
                            )
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = if (compact) 6.dp else 8.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp),
                    ) {
                        item(key = "job-header") {
                            JobDetailHeader(
                                repoName = uiState.repoName,
                                status = uiState.status,
                                currentStep = uiState.currentStep,
                                totalSteps = uiState.totalSteps,
                                startedAtEpochMs = uiState.startedAtEpochMs,
                                updatedAtEpochMs = uiState.updatedAtEpochMs,
                                compact = compact,
                            )
                        }
                        when (JobDetailTab.entries[selectedTab]) {
                            JobDetailTab.Overview -> jobDetailOverviewItems(
                                pendingRequests = uiState.pendingRequests,
                                agentCount = uiState.agents.size,
                                onReview = onReview,
                            )
                            JobDetailTab.Agents -> jobDetailAgentsItems(
                                agents = uiState.agents,
                                onAgentClick = onAgentClick,
                            )
                            JobDetailTab.Activity -> jobDetailActivityItems(
                                activityEvents = uiState.activityEvents,
                            )
                        }
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
