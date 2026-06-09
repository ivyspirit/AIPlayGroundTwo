package com.example.aiplaygroundtwo.domain.model

data class ActivityEvent(
    val id: String,
    val jobId: String,
    val agentId: String,
    val agentName: String,
    val message: String,
    val occurredAtEpochMs: Long,
)
