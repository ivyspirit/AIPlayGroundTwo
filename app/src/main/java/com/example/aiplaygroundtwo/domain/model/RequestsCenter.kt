package com.example.aiplaygroundtwo.domain.model

data class RequestsCenter(
    val pendingByJob: List<PendingJobGroup>,
    val history: List<ReviewRequest>,
)

data class PendingJobGroup(
    val jobId: String,
    val jobTitle: String,
    val repoName: String,
    val pendingCount: Int,
    val requests: List<ReviewRequest>,
)
