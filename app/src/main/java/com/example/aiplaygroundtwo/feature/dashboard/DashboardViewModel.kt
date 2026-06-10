package com.example.aiplaygroundtwo.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiplaygroundtwo.data.network.NetworkResult
import com.example.aiplaygroundtwo.data.repository.AgentRepository
import com.example.aiplaygroundtwo.di.DispatcherProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardViewModel(
    private val repository: AgentRepository,
    private val dispatchers: DispatcherProvider,
) : ViewModel() {
    private val isRefreshing = MutableStateFlow(false)
    private val refreshError = MutableStateFlow<String?>(null)
    private val snackbarMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.observeJobs(),
        isRefreshing,
        refreshError,
        snackbarMessage,
    ) { jobs, refreshing, error, snackbar ->
        val pendingRequestCount = jobs.sumOf { job ->
            job.pendingApprovalCount + job.pendingNeedsInputCount
        }

        when {
            error != null && jobs.isEmpty() -> DashboardUiState.Error(error)
            jobs.isEmpty() && refreshing -> DashboardUiState.Loading
            jobs.isEmpty() -> DashboardUiState.Empty
            else -> DashboardUiState.Content(
                jobs = jobs,
                pendingRequestCount = pendingRequestCount,
                isRefreshing = refreshing,
                snackbarMessage = snackbar,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState.Loading,
    )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                isRefreshing.value = true
                refreshError.value = null
                when (val result = repository.refresh()) {
                    is NetworkResult.Success -> {
                        refreshError.value = null
                        snackbarMessage.value = null
                    }
                    is NetworkResult.HttpError -> handleRefreshFailure(result.message)
                    is NetworkResult.NetworkError -> {
                        handleRefreshFailure(
                            result.cause.message ?: "Network error",
                        )
                    }
                }
                isRefreshing.value = false
            }
        }
    }

    fun dismissSnackbar() {
        snackbarMessage.value = null
    }

    private suspend fun handleRefreshFailure(message: String) {
        val hasCachedJobs = repository.observeJobs().first().isNotEmpty()
        if (hasCachedJobs) {
            snackbarMessage.value = message
            refreshError.value = null
        } else {
            refreshError.value = message
            snackbarMessage.value = null
        }
    }
}
