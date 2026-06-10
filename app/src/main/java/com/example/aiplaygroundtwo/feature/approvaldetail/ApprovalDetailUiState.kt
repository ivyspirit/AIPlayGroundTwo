package com.example.aiplaygroundtwo.feature.approvaldetail

import com.example.aiplaygroundtwo.domain.model.ReviewRequest

sealed interface ApprovalDetailUiState {
    data object Loading : ApprovalDetailUiState

    data class Content(
        val request: ReviewRequest,
        val jobTitle: String,
        val repoName: String,
        val isSubmitting: Boolean,
        val submitError: String?,
        val isAlreadyResolved: Boolean,
    ) : ApprovalDetailUiState

    data class Error(val message: String) : ApprovalDetailUiState
}
