package com.example.aiplaygroundtwo.feature.dashboard

import com.example.aiplaygroundtwo.domain.model.JobSummary

sealed interface DashboardUiState {
    data object Loading : DashboardUiState

    data class Content(
        val jobs: List<JobSummary>,
        val pendingRequestCount: Int,
        val isRefreshing: Boolean = false,
    ) : DashboardUiState

    data class Error(
        val message: String,
    ) : DashboardUiState

    data object Empty : DashboardUiState
}
