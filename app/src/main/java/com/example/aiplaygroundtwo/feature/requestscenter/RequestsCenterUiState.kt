package com.example.aiplaygroundtwo.feature.requestscenter

import com.example.aiplaygroundtwo.domain.model.PendingJobGroup
import com.example.aiplaygroundtwo.domain.model.ReviewRequest

sealed interface RequestsCenterUiState {
    data object Loading : RequestsCenterUiState

    data class Content(
        val pendingCount: Int,
        val pendingByJob: List<PendingJobGroup>,
        val history: List<ReviewRequest>,
        val historyPreview: ReviewRequest?,
    ) : RequestsCenterUiState
}
