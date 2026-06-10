package com.example.aiplaygroundtwo.feature.requestscenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiplaygroundtwo.data.repository.AgentRepository
import com.example.aiplaygroundtwo.domain.model.RequestsCenter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RequestsCenterViewModel(
    private val repository: AgentRepository,
) : ViewModel() {
    val uiState: StateFlow<RequestsCenterUiState> = repository.observeRequestsCenter()
        .map { center -> center.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = RequestsCenterUiState.Loading,
        )

    private fun RequestsCenter.toUiState(): RequestsCenterUiState {
        val pendingCount = pendingByJob.sumOf { it.pendingCount }
        return RequestsCenterUiState.Content(
            pendingCount = pendingCount,
            pendingByJob = pendingByJob,
            history = history,
            historyPreview = history.firstOrNull(),
        )
    }
}
