package com.example.aiplaygroundtwo.domain.model

data class JobSummary(
    val id: String,
    val title: String,
    val repoName: String,
    val status: JobStatus,
    val currentStep: Int,
    val totalSteps: Int,
    val agentCount: Int,
    val pendingApprovalCount: Int,
    val pendingNeedsInputCount: Int,
)
