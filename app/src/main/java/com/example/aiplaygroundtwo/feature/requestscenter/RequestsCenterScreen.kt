package com.example.aiplaygroundtwo.feature.requestscenter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aiplaygroundtwo.domain.model.PendingJobGroup
import com.example.aiplaygroundtwo.domain.model.ReviewRequest
import com.example.aiplaygroundtwo.ui.components.AgentEmptyState
import com.example.aiplaygroundtwo.ui.components.AgentLoadingState
import com.example.aiplaygroundtwo.ui.components.OutcomeChip
import com.example.aiplaygroundtwo.ui.components.RiskChip
import com.example.aiplaygroundtwo.ui.components.TypeChip
import com.example.aiplaygroundtwo.ui.placeholder.BackIcon

private enum class RequestsCenterTab(val label: String) {
    Pending("Pending"),
    History("History"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsCenterScreen(
    uiState: RequestsCenterUiState,
    onBack: () -> Unit,
    onReview: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        RequestsCenterUiState.Loading -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    RequestsCenterTopBar(pendingCount = 0, onBack = onBack)
                },
            ) { innerPadding ->
                AgentLoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }
        }
        is RequestsCenterUiState.Content -> {
            var selectedTab by rememberSaveable { mutableIntStateOf(0) }
            Scaffold(
                modifier = modifier,
                topBar = {
                    RequestsCenterTopBar(
                        pendingCount = uiState.pendingCount,
                        onBack = onBack,
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    TabRow(selectedTabIndex = selectedTab) {
                        RequestsCenterTab.entries.forEachIndexed { index, tab ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(tab.label) },
                            )
                        }
                    }
                    val tabModifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                    when (RequestsCenterTab.entries[selectedTab]) {
                        RequestsCenterTab.Pending -> PendingTabContent(
                            pendingByJob = uiState.pendingByJob,
                            historyPreview = uiState.historyPreview,
                            onReview = onReview,
                            modifier = tabModifier,
                        )
                        RequestsCenterTab.History -> HistoryTabContent(
                            history = uiState.history,
                            modifier = tabModifier,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RequestsCenterTopBar(
    pendingCount: Int,
    onBack: () -> Unit,
) {
    TopAppBar(
        title = { Text("Requests ($pendingCount)") },
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

@Composable
private fun PendingTabContent(
    pendingByJob: List<PendingJobGroup>,
    historyPreview: ReviewRequest?,
    onReview: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (pendingByJob.isEmpty()) {
        AgentEmptyState(
            message = "All caught up",
            modifier = modifier,
        )
        return
    }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        pendingByJob.forEach { group ->
            item(key = "header-${group.jobId}") {
                JobGroupHeader(group = group)
            }
            items(group.requests, key = { it.id }) { request ->
                PendingRequestRow(request = request, onReview = { onReview(request.id) })
            }
        }
        if (historyPreview != null) {
            item(key = "history-preview-header") {
                Text(
                    text = "History (preview)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            item(key = "history-preview-${historyPreview.id}") {
                HistoryRequestRow(request = historyPreview)
            }
        }
    }
}

@Composable
private fun HistoryTabContent(
    history: List<ReviewRequest>,
    modifier: Modifier = Modifier,
) {
    if (history.isEmpty()) {
        AgentEmptyState(
            message = "No history yet",
            modifier = modifier,
        )
        return
    }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(history, key = { it.id }) { request ->
            HistoryRequestRow(request = request)
        }
    }
}

@Composable
private fun JobGroupHeader(
    group: PendingJobGroup,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = group.jobTitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Text(
                    text = "${group.pendingCount}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        Text(
            text = "Repo: ${group.repoName}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun PendingRequestRow(
    request: ReviewRequest,
    onReview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = request.agentName,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                )
                TypeChip(type = request.type)
            }
            Text(
                text = request.title,
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RiskChip(risk = request.risk)
                Button(onClick = onReview) {
                    Text("Review")
                }
            }
        }
    }
}

@Composable
private fun HistoryRequestRow(
    request: ReviewRequest,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = request.agentName, style = MaterialTheme.typography.titleSmall)
                    TypeChip(type = request.type)
                    OutcomeChip(status = request.status, selectedOption = request.selectedOption)
                }
                Text(
                    text = request.title,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
                request.feedback?.let { feedback ->
                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
            Text(
                text = "›",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
