package com.example.aiplaygroundtwo.data.repository

import androidx.room.withTransaction
import com.example.aiplaygroundtwo.data.local.AgentDatabase
import com.example.aiplaygroundtwo.data.mapper.SnapshotEntities
import com.example.aiplaygroundtwo.data.mapper.toDto
import com.example.aiplaygroundtwo.data.mapper.toEntities
import com.example.aiplaygroundtwo.data.mapper.toDomain
import com.example.aiplaygroundtwo.data.mapper.toJobDetail
import com.example.aiplaygroundtwo.data.mapper.toJobSummary
import com.example.aiplaygroundtwo.data.mapper.toRequestsCenter
import com.example.aiplaygroundtwo.data.network.AgentNetworkApi
import com.example.aiplaygroundtwo.data.network.NetworkResult
import com.example.aiplaygroundtwo.di.DispatcherProvider
import com.example.aiplaygroundtwo.domain.model.JobDetail
import com.example.aiplaygroundtwo.domain.model.JobSummary
import com.example.aiplaygroundtwo.domain.model.RequestsCenter
import com.example.aiplaygroundtwo.domain.model.ReviewRequest
import com.example.aiplaygroundtwo.domain.model.ReviewResolution
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DefaultAgentRepository(
    private val database: AgentDatabase,
    private val networkApi: AgentNetworkApi,
    private val dispatchers: DispatcherProvider,
) : AgentRepository {
    private val jobDao = database.jobDao()
    private val agentDao = database.agentDao()
    private val reviewRequestDao = database.reviewRequestDao()
    private val activityEventDao = database.activityEventDao()

    override fun observeJobs(): Flow<List<JobSummary>> = combine(
        jobDao.observeAll(),
        agentDao.observeAll(),
        reviewRequestDao.observeAllPending(),
    ) { jobs, agents, pending ->
        jobs.map { job ->
            val jobAgents = agents.filter { it.jobId == job.id }
            val jobPending = pending.filter { it.jobId == job.id }
            job.toJobSummary(
                agentCount = jobAgents.size,
                pendingApprovalCount = jobPending.count { it.type == "APPROVAL" },
                pendingNeedsInputCount = jobPending.count { it.type == "NEEDS_INPUT" },
            )
        }
    }

    override fun observeJobDetail(jobId: String): Flow<JobDetail?> = combine(
        jobDao.observeById(jobId),
        agentDao.observeForJob(jobId),
        reviewRequestDao.observePendingForJob(jobId),
        activityEventDao.observeForJob(jobId),
    ) { job, agents, pending, events ->
        job?.toJobDetail(
            agents = agents.map { it.toDomain() },
            pendingRequests = pending.map { it.toDomain() },
            activityEvents = events.map { it.toDomain() },
        )
    }

    override fun observeRequestsCenter(): Flow<RequestsCenter> = combine(
        jobDao.observeAll(),
        reviewRequestDao.observeAllPending(),
        reviewRequestDao.observeHistory(),
    ) { jobs, pendingEntities, historyEntities ->
        val pending = pendingEntities.map { it.toDomain() }
        val history = historyEntities.map { it.toDomain() }
        (pending + history).toRequestsCenter(jobs)
    }

    override fun observeRequestDetail(requestId: String): Flow<ReviewRequest?> =
        reviewRequestDao.observeById(requestId).map { it?.toDomain() }

    override suspend fun refresh(): NetworkResult<Unit> = withContext(dispatchers.io) {
        when (val result = networkApi.getSnapshot()) {
            is NetworkResult.Success -> {
                replaceSnapshot(result.data.toEntities())
                NetworkResult.Success(Unit)
            }
            is NetworkResult.HttpError -> result
            is NetworkResult.NetworkError -> result
        }
    }

    override suspend fun submitReviewResolution(
        resolution: ReviewResolution,
    ): NetworkResult<Unit> = withContext(dispatchers.io) {
        when (val result = networkApi.submitReviewResolution(resolution.toDto())) {
            is NetworkResult.Success -> {
                replaceSnapshot(result.data.toEntities())
                NetworkResult.Success(Unit)
            }
            is NetworkResult.HttpError -> result
            is NetworkResult.NetworkError -> result
        }
    }

    override suspend fun seedIfEmpty() = withContext(dispatchers.io) {
        if (jobDao.count() == 0) {
            refresh()
        }
    }

    private suspend fun replaceSnapshot(entities: SnapshotEntities) {
        database.withTransaction {
            jobDao.deleteAll()
            jobDao.upsertAll(entities.jobs)
            agentDao.upsertAll(entities.agents)
            reviewRequestDao.upsertAll(entities.reviewRequests)
            activityEventDao.upsertAll(entities.activityEvents)
        }
    }
}
