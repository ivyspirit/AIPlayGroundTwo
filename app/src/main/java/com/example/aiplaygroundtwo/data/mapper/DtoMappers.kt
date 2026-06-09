package com.example.aiplaygroundtwo.data.mapper

import com.example.aiplaygroundtwo.data.local.entity.ActivityEventEntity
import com.example.aiplaygroundtwo.data.local.entity.AgentEntity
import com.example.aiplaygroundtwo.data.local.entity.JobEntity
import com.example.aiplaygroundtwo.data.local.entity.ReviewRequestEntity
import com.example.aiplaygroundtwo.data.network.dto.ActivityEventDto
import com.example.aiplaygroundtwo.data.network.dto.AgentDto
import com.example.aiplaygroundtwo.data.network.dto.JobDto
import com.example.aiplaygroundtwo.data.network.dto.ReviewRequestDto
import com.example.aiplaygroundtwo.data.network.dto.SyncPayloadDto
import com.example.aiplaygroundtwo.domain.model.ResolutionAction
import com.example.aiplaygroundtwo.domain.model.ReviewResolution

fun SyncPayloadDto.toEntities(): SnapshotEntities = SnapshotEntities(
    jobs = jobs.map { it.toEntity() },
    agents = agents.map { it.toEntity() },
    reviewRequests = reviewRequests.map { it.toEntity() },
    activityEvents = activityEvents.map { it.toEntity() },
)

data class SnapshotEntities(
    val jobs: List<JobEntity>,
    val agents: List<AgentEntity>,
    val reviewRequests: List<ReviewRequestEntity>,
    val activityEvents: List<ActivityEventEntity>,
)

fun JobDto.toEntity(): JobEntity = JobEntity(
    id = id,
    title = title,
    repoName = repoName,
    status = status,
    currentStep = currentStep,
    totalSteps = totalSteps,
    startedAtEpochMs = startedAtEpochMs,
    updatedAtEpochMs = updatedAtEpochMs,
)

fun AgentDto.toEntity(): AgentEntity = AgentEntity(
    id = id,
    jobId = jobId,
    name = name,
    role = role,
    status = status,
    currentSummary = currentSummary,
    pendingRequestId = pendingRequestId,
)

fun ReviewRequestDto.toEntity(): ReviewRequestEntity = ReviewRequestEntity(
    id = id,
    jobId = jobId,
    agentId = agentId,
    agentName = agentName,
    type = type,
    status = status,
    title = title,
    risk = risk,
    reasoning = reasoning,
    requestedAtEpochMs = requestedAtEpochMs,
    proposedAction = proposedAction,
    affectedFiles = affectedFiles,
    question = question,
    options = options,
    selectedOption = selectedOption,
    feedback = feedback,
)

fun ActivityEventDto.toEntity(): ActivityEventEntity = ActivityEventEntity(
    id = id,
    jobId = jobId,
    agentId = agentId,
    agentName = agentName,
    message = message,
    occurredAtEpochMs = occurredAtEpochMs,
)

fun ReviewResolution.toDto(): com.example.aiplaygroundtwo.data.network.dto.ReviewResolutionDto =
    com.example.aiplaygroundtwo.data.network.dto.ReviewResolutionDto(
        requestId = requestId,
        action = when (action) {
            ResolutionAction.Approve -> "APPROVE"
            ResolutionAction.Reject -> "REJECT"
            ResolutionAction.Continue -> "CONTINUE"
        },
        feedback = feedback,
        selectedOption = selectedOption,
    )
