package com.example.aiplaygroundtwo.domain.model

data class JobDetail(
    val id: String,
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
)
