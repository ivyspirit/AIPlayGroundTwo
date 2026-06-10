package com.example.aiplaygroundtwo.feature.jobdetail

import com.example.aiplaygroundtwo.domain.model.ActivityEvent
import com.example.aiplaygroundtwo.domain.model.AgentStatus
import com.example.aiplaygroundtwo.domain.model.ReviewRequest

data class InspectorUiState(
    val agentId: String,
    val agentName: String,
    val agentStatus: AgentStatus,
    val role: String,
    val jobTitle: String,
    val currentSummary: String,
    val pendingRequest: ReviewRequest?,
    val recentActions: List<ActivityEvent>,
) {
    val showReview: Boolean = pendingRequest != null
}
