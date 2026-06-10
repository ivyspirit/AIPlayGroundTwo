package com.example.aiplaygroundtwo.feature.approvaldetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiplaygroundtwo.data.network.NetworkResult
import com.example.aiplaygroundtwo.data.repository.AgentRepository
import com.example.aiplaygroundtwo.di.DispatcherProvider
import com.example.aiplaygroundtwo.domain.model.ResolutionAction
import com.example.aiplaygroundtwo.domain.model.ReviewResolution
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApprovalDetailViewModel(
    private val repository: AgentRepository,
    private val requestId: String,
    private val dispatchers: DispatcherProvider,
) : ViewModel() {
    private val isSubmitting = MutableStateFlow(false)
    private val submitError = MutableStateFlow<String?>(null)
    private val navigateBack = MutableStateFlow(false)

    val uiState: StateFlow<ApprovalDetailUiState> = combine(
        repository.observeRequestDetail(requestId),
        repository.observeJobs(),
        isSubmitting,
        submitError,
    ) { request, jobs, submitting, error ->
        when (request) {
            null -> ApprovalDetailUiState.Loading
            else -> {
                val job = jobs.find { it.id == request.jobId }
                ApprovalDetailUiState.Content(
                    request = request,
                    jobTitle = job?.title ?: "",
                    repoName = job?.repoName ?: "",
                    isSubmitting = submitting,
                    submitError = error,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ApprovalDetailUiState.Loading,
    )

    val shouldNavigateBack: StateFlow<Boolean> = navigateBack.asStateFlow()

    fun approve(feedback: String) {
        submit(ResolutionAction.Approve, feedback, null)
    }

    fun reject(feedback: String) {
        submit(ResolutionAction.Reject, feedback, null)
    }

    fun continueWithSelection(selectedOption: String, feedback: String) {
        submit(ResolutionAction.Continue, feedback, selectedOption)
    }

    fun onNavigateBackHandled() {
        navigateBack.value = false
    }

    private fun submit(
        action: ResolutionAction,
        feedback: String,
        selectedOption: String?,
    ) {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                isSubmitting.value = true
                submitError.value = null
                val resolution = ReviewResolution(
                    requestId = requestId,
                    action = action,
                    feedback = feedback.ifBlank { null },
                    selectedOption = selectedOption,
                )
                when (val result = repository.submitReviewResolution(resolution)) {
                    is NetworkResult.Success -> navigateBack.value = true
                    is NetworkResult.HttpError -> submitError.value = result.message
                    is NetworkResult.NetworkError -> {
                        submitError.value = result.cause.message ?: "Network error"
                    }
                }
                isSubmitting.value = false
            }
        }
    }
}
