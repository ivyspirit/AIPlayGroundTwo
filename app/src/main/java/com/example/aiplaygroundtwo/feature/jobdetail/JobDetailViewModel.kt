package com.example.aiplaygroundtwo.feature.jobdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiplaygroundtwo.data.repository.AgentRepository
import com.example.aiplaygroundtwo.domain.model.JobDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class JobDetailViewModel(
    private val repository: AgentRepository,
    private val jobId: String,
) : ViewModel() {
    private val selectedAgentId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<JobDetailUiState> = repository.observeJobDetail(jobId)
        .map { detail ->
            when (detail) {
                null -> JobDetailUiState.Loading
                else -> detail.toContent()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = JobDetailUiState.Loading,
        )

    val inspectorState: StateFlow<InspectorUiState?> = combine(
        uiState,
        selectedAgentId,
    ) { state, agentId ->
        when {
            state !is JobDetailUiState.Content || agentId == null -> null
            else -> mapInspector(state, agentId)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    fun onAgentClick(agentId: String) {
        selectedAgentId.value = agentId
    }

    fun dismissInspector() {
        selectedAgentId.value = null
    }

    private fun JobDetail.toContent(): JobDetailUiState.Content = JobDetailUiState.Content(
        title = title,
        repoName = repoName,
        status = status,
        currentStep = currentStep,
        totalSteps = totalSteps,
        startedAtEpochMs = startedAtEpochMs,
        updatedAtEpochMs = updatedAtEpochMs,
        agents = agents,
        pendingRequests = pendingRequests,
        activityEvents = activityEvents,
    )

    private fun mapInspector(
        content: JobDetailUiState.Content,
        agentId: String,
    ): InspectorUiState? {
        val agent = content.agents.find { it.id == agentId } ?: return null
        val pendingRequest = content.pendingRequests.find { it.agentId == agentId }
            ?: agent.pendingRequestId?.let { requestId ->
                content.pendingRequests.find { it.id == requestId }
            }
        val recentActions = content.activityEvents
            .filter { it.agentId == agentId }
            .sortedByDescending { it.occurredAtEpochMs }
        return InspectorUiState(
            agentId = agent.id,
            agentName = agent.name,
            agentStatus = agent.status,
            role = agent.role,
            jobTitle = content.title,
            currentSummary = agent.currentSummary,
            pendingRequest = pendingRequest,
            recentActions = recentActions,
        )
    }
}
