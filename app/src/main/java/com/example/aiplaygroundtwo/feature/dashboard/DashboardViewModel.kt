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
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardViewModel(
    private val repository: AgentRepository,
    private val dispatchers: DispatcherProvider,
) : ViewModel() {
    private val isRefreshing = MutableStateFlow(false)
    private val refreshError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.observeJobs(),
        isRefreshing,
        refreshError,
    ) { jobs, refreshing, error ->
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
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000),
        initialValue = DashboardUiState.Loading,
    )

//    init {
//        refresh()
//    }

    fun refresh() {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                isRefreshing.value = true
                refreshError.value = null
                when (val result = repository.refresh()) {
                    is NetworkResult.Success -> refreshError.value = null
                    is NetworkResult.HttpError -> refreshError.value = result.message
                    is NetworkResult.NetworkError -> {
                        refreshError.value = result.cause.message ?: "Network error"
                    }
                }
                isRefreshing.value = false
            }
        }
    }
}
