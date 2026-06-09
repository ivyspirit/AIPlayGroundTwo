package com.example.aiplaygroundtwo.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aiplaygroundtwo.data.repository.AgentRepository
import com.example.aiplaygroundtwo.di.DispatcherProvider

class DashboardViewModelFactory(
    private val repository: AgentRepository,
    private val dispatchers: DispatcherProvider,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(repository, dispatchers) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
