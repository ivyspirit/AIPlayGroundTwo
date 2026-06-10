package com.example.aiplaygroundtwo.feature.dashboard

import com.example.aiplaygroundtwo.data.network.NetworkResult
import com.example.aiplaygroundtwo.data.repository.AgentRepository
import com.example.aiplaygroundtwo.domain.model.JobDetail
import com.example.aiplaygroundtwo.domain.model.JobSummary
import com.example.aiplaygroundtwo.domain.model.RequestsCenter
import com.example.aiplaygroundtwo.domain.model.ReviewRequest
import com.example.aiplaygroundtwo.domain.model.ReviewResolution
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeAgentRepository : AgentRepository {
    private val jobs = MutableStateFlow<List<JobSummary>>(emptyList())
    private val jobDetails = MutableStateFlow<Map<String, JobDetail>>(emptyMap())
    var refreshResult: NetworkResult<Unit> = NetworkResult.Success(Unit)

    fun setJobs(value: List<JobSummary>) {
        jobs.value = value
    }

    fun setJobDetail(jobId: String, detail: JobDetail) {
        jobDetails.value = jobDetails.value + (jobId to detail)
    }

    override fun observeJobs(): Flow<List<JobSummary>> = jobs

    override fun observeJobDetail(jobId: String): Flow<JobDetail?> =
        jobDetails.map { it[jobId] }

    override fun observeRequestsCenter(): Flow<RequestsCenter> =
        MutableStateFlow(RequestsCenter(pendingByJob = emptyList(), history = emptyList()))

    override fun observeRequestDetail(requestId: String): Flow<ReviewRequest?> =
        MutableStateFlow(null)

    override suspend fun refresh(): NetworkResult<Unit> = refreshResult

    override suspend fun submitReviewResolution(
        resolution: ReviewResolution,
    ): NetworkResult<Unit> = NetworkResult.Success(Unit)

    override suspend fun seedIfEmpty() = Unit
}
