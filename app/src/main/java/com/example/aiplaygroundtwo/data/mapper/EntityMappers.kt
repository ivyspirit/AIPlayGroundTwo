package com.example.aiplaygroundtwo.data.mapper

import com.example.aiplaygroundtwo.data.local.entity.ActivityEventEntity
import com.example.aiplaygroundtwo.data.local.entity.AgentEntity
import com.example.aiplaygroundtwo.data.local.entity.JobEntity
import com.example.aiplaygroundtwo.data.local.entity.ReviewRequestEntity
import com.example.aiplaygroundtwo.domain.model.Agent
import com.example.aiplaygroundtwo.domain.model.AgentStatus
import com.example.aiplaygroundtwo.domain.model.ActivityEvent
import com.example.aiplaygroundtwo.domain.model.JobDetail
import com.example.aiplaygroundtwo.domain.model.JobStatus
import com.example.aiplaygroundtwo.domain.model.JobSummary
import com.example.aiplaygroundtwo.domain.model.PendingJobGroup
import com.example.aiplaygroundtwo.domain.model.RequestStatus
import com.example.aiplaygroundtwo.domain.model.RequestType
import com.example.aiplaygroundtwo.domain.model.RequestsCenter
import com.example.aiplaygroundtwo.domain.model.ReviewRequest
import com.example.aiplaygroundtwo.domain.model.RiskLevel

fun JobEntity.toJobSummary(
    agentCount: Int,
    pendingApprovalCount: Int,
    pendingNeedsInputCount: Int,
): JobSummary = JobSummary(
    id = id,
    title = title,
    repoName = repoName,
    status = status.toJobStatus(),
    currentStep = currentStep,
    totalSteps = totalSteps,
    agentCount = agentCount,
    pendingApprovalCount = pendingApprovalCount,
    pendingNeedsInputCount = pendingNeedsInputCount,
)

fun JobEntity.toJobDetail(
    agents: List<Agent>,
    pendingRequests: List<ReviewRequest>,
    activityEvents: List<ActivityEvent>,
): JobDetail = JobDetail(
    id = id,
    title = title,
    repoName = repoName,
    status = status.toJobStatus(),
    currentStep = currentStep,
    totalSteps = totalSteps,
    startedAtEpochMs = startedAtEpochMs,
    updatedAtEpochMs = updatedAtEpochMs,
    agents = agents,
    pendingRequests = pendingRequests,
    activityEvents = activityEvents,
)

fun AgentEntity.toDomain(): Agent = Agent(
    id = id,
    jobId = jobId,
    name = name,
    role = role,
    status = status.toAgentStatus(),
    currentSummary = currentSummary,
    pendingRequestId = pendingRequestId,
)

fun ReviewRequestEntity.toDomain(): ReviewRequest = ReviewRequest(
    id = id,
    jobId = jobId,
    agentId = agentId,
    agentName = agentName,
    type = type.toRequestType(),
    status = status.toRequestStatus(),
    title = title,
    risk = risk.toRiskLevel(),
    reasoning = reasoning,
    requestedAtEpochMs = requestedAtEpochMs,
    proposedAction = proposedAction,
    affectedFiles = affectedFiles,
    question = question,
    options = options,
    selectedOption = selectedOption,
    feedback = feedback,
)

fun ActivityEventEntity.toDomain(): ActivityEvent = ActivityEvent(
    id = id,
    jobId = jobId,
    agentId = agentId,
    agentName = agentName,
    message = message,
    occurredAtEpochMs = occurredAtEpochMs,
)

fun List<ReviewRequest>.toRequestsCenter(
    jobs: List<JobEntity>,
): RequestsCenter {
    val jobById = jobs.associateBy { it.id }
    val pending = filter { it.status == RequestStatus.Pending }
    val history = filter { it.status != RequestStatus.Pending }
        .sortedByDescending { it.requestedAtEpochMs }
    val pendingByJob = pending
        .groupBy { it.jobId }
        .map { (jobId, requests) ->
            val job = jobById[jobId]
            PendingJobGroup(
                jobId = jobId,
                jobTitle = job?.title ?: "",
                repoName = job?.repoName ?: "",
                pendingCount = requests.size,
                requests = requests,
            )
        }
        .sortedBy { it.jobTitle }
    return RequestsCenter(
        pendingByJob = pendingByJob,
        history = history,
    )
}

private fun String.toJobStatus(): JobStatus = when (this) {
    "BLOCKED" -> JobStatus.Blocked
    "RUNNING" -> JobStatus.Running
    "COMPLETED" -> JobStatus.Completed
    else -> JobStatus.Running
}

private fun String.toAgentStatus(): AgentStatus = when (this) {
    "COMPLETED" -> AgentStatus.Completed
    "BLOCKED" -> AgentStatus.Blocked
    "RUNNING" -> AgentStatus.Running
    "IDLE" -> AgentStatus.Idle
    else -> AgentStatus.Running
}

private fun String.toRequestType(): RequestType = when (this) {
    "APPROVAL" -> RequestType.Approval
    "NEEDS_INPUT" -> RequestType.NeedsInput
    else -> RequestType.Approval
}

private fun String.toRequestStatus(): RequestStatus = when (this) {
    "PENDING" -> RequestStatus.Pending
    "APPROVED" -> RequestStatus.Approved
    "REJECTED" -> RequestStatus.Rejected
    "INPUT_SELECTED" -> RequestStatus.InputSelected
    else -> RequestStatus.Pending
}

private fun String.toRiskLevel(): RiskLevel = when (this) {
    "HIGH" -> RiskLevel.High
    "MEDIUM" -> RiskLevel.Medium
    "LOW" -> RiskLevel.Low
    else -> RiskLevel.Medium
}
