package com.example.aiplaygroundtwo.domain.model

data class Agent(
    val id: String,
    val jobId: String,
    val name: String,
    val role: String,
    val status: AgentStatus,
    val currentSummary: String,
    val pendingRequestId: String?,
)
