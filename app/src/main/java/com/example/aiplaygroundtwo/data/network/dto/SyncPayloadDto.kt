package com.example.aiplaygroundtwo.data.network.dto

data class SyncPayloadDto(
    val jobs: List<JobDto>,
    val agents: List<AgentDto>,
    val reviewRequests: List<ReviewRequestDto>,
    val activityEvents: List<ActivityEventDto>,
)

data class JobDto(
    val id: String,
    val title: String,
    val repoName: String,
    val status: String,
    val currentStep: Int,
    val totalSteps: Int,
    val startedAtEpochMs: Long,
    val updatedAtEpochMs: Long,
)

data class AgentDto(
    val id: String,
    val jobId: String,
    val name: String,
    val role: String,
    val status: String,
    val currentSummary: String,
    val pendingRequestId: String?,
)

data class ReviewRequestDto(
    val id: String,
    val jobId: String,
    val agentId: String,
    val agentName: String,
    val type: String,
    val status: String,
    val title: String,
    val risk: String,
    val reasoning: String,
    val requestedAtEpochMs: Long,
    val proposedAction: String?,
    val affectedFiles: List<String>?,
    val question: String?,
    val options: List<String>?,
    val selectedOption: String?,
    val feedback: String?,
)

data class ActivityEventDto(
    val id: String,
    val jobId: String,
    val agentId: String,
    val agentName: String,
    val message: String,
    val occurredAtEpochMs: Long,
)
