package com.example.aiplaygroundtwo.domain.model

data class ReviewRequest(
    val id: String,
    val jobId: String,
    val agentId: String,
    val agentName: String,
    val type: RequestType,
    val status: RequestStatus,
    val title: String,
    val risk: RiskLevel,
    val reasoning: String,
    val requestedAtEpochMs: Long,
    val proposedAction: String?,
    val affectedFiles: List<String>?,
    val question: String?,
    val options: List<String>?,
    val selectedOption: String?,
    val feedback: String?,
)
