package com.example.aiplaygroundtwo.feature.jobdetail

import com.example.aiplaygroundtwo.domain.model.ActivityEvent
import com.example.aiplaygroundtwo.domain.model.Agent
import com.example.aiplaygroundtwo.domain.model.JobStatus
import com.example.aiplaygroundtwo.domain.model.ReviewRequest

sealed interface JobDetailUiState {
    data object Loading : JobDetailUiState

    data class Content(
        val title: String,
        val repoName: String,
        val status: JobStatus,
        val currentStep: Int,
        val totalSteps: Int,
        val startedAtEpochMs: Long,
        val updatedAtEpochMs: Long,
        val agents: List<Agent>,
        val pendingRequests: List<ReviewRequest>,
        val activityEvents: List<ActivityEvent>,
    ) : JobDetailUiState
}
