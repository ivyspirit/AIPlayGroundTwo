package com.example.aiplaygroundtwo.feature.approvaldetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aiplaygroundtwo.data.repository.AgentRepository
import com.example.aiplaygroundtwo.di.DispatcherProvider

class ApprovalDetailViewModelFactory(
    private val repository: AgentRepository,
    private val requestId: String,
    private val dispatchers: DispatcherProvider,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApprovalDetailViewModel::class.java)) {
            return ApprovalDetailViewModel(repository, requestId, dispatchers) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
