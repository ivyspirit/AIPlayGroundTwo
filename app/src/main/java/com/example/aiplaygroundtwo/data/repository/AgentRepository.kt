package com.example.aiplaygroundtwo.data.repository

import com.example.aiplaygroundtwo.data.network.NetworkResult
import com.example.aiplaygroundtwo.domain.model.JobDetail
import com.example.aiplaygroundtwo.domain.model.JobSummary
import com.example.aiplaygroundtwo.domain.model.RequestsCenter
import com.example.aiplaygroundtwo.domain.model.ReviewRequest
import com.example.aiplaygroundtwo.domain.model.ReviewResolution
import kotlinx.coroutines.flow.Flow

interface AgentRepository {
    fun observeJobs(): Flow<List<JobSummary>>
    fun observeJobDetail(jobId: String): Flow<JobDetail?>
    fun observeRequestsCenter(): Flow<RequestsCenter>
    fun observeRequestDetail(requestId: String): Flow<ReviewRequest?>
    suspend fun refresh(): NetworkResult<Unit>
    suspend fun submitReviewResolution(resolution: ReviewResolution): NetworkResult<Unit>
    suspend fun seedIfEmpty()
}
