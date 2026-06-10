package com.example.aiplaygroundtwo.feature.requestscenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aiplaygroundtwo.data.repository.AgentRepository

class RequestsCenterViewModelFactory(
    private val repository: AgentRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestsCenterViewModel::class.java)) {
            return RequestsCenterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
