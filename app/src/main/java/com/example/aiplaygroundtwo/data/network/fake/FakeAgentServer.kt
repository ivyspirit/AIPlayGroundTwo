package com.example.aiplaygroundtwo.data.network.fake

import com.example.aiplaygroundtwo.data.network.dto.ActivityEventDto
import com.example.aiplaygroundtwo.data.network.dto.AgentDto
import com.example.aiplaygroundtwo.data.network.dto.JobDto
import com.example.aiplaygroundtwo.data.network.dto.ReviewRequestDto
import com.example.aiplaygroundtwo.data.network.dto.ReviewResolutionDto
import com.example.aiplaygroundtwo.data.network.dto.SyncPayloadDto

class FakeAgentServer {
  private val jobs = mutableListOf<JobDto>()
  private val agents = mutableListOf<AgentDto>()
  private val reviewRequests = mutableListOf<ReviewRequestDto>()
  private val activityEvents = mutableListOf<ActivityEventDto>()
  private var nextEventId = 100

  init {
    seed()
  }

  fun buildSnapshot(): SyncPayloadDto = SyncPayloadDto(
    jobs = jobs.toList(),
    agents = agents.toList(),
    reviewRequests = reviewRequests.toList(),
    activityEvents = activityEvents.toList(),
  )

  fun resolve(resolution: ReviewResolutionDto): ResolveResult {
    val request = reviewRequests.find { it.id == resolution.requestId }
      ?: return ResolveResult.HttpError(404, "Request not found")

    if (request.status != "PENDING") {
      return ResolveResult.HttpError(400, "Request is not pending")
    }

    when (request.type) {
      "APPROVAL" -> when (resolution.action) {
        "APPROVE" -> applyApprovalApproved(request, resolution.feedback)
        "REJECT" -> applyApprovalRejected(request, resolution.feedback)
        else -> return ResolveResult.HttpError(400, "Invalid action for approval request")
      }
      "NEEDS_INPUT" -> when (resolution.action) {
        "CONTINUE" -> {
          if (resolution.selectedOption.isNullOrBlank()) {
            return ResolveResult.HttpError(400, "Selected option is required")
          }
          if (request.options?.contains(resolution.selectedOption) != true) {
            return ResolveResult.HttpError(400, "Selected option is invalid")
          }
          applyNeedsInputContinued(request, resolution.selectedOption, resolution.feedback)
        }
        "REJECT" -> applyNeedsInputRejected(request, resolution.feedback)
        else -> return ResolveResult.HttpError(400, "Invalid action for needs-input request")
      }
      else -> return ResolveResult.HttpError(400, "Unknown request type")
    }

    rederiveJobStatuses()
    return ResolveResult.Success
  }

  private fun applyApprovalApproved(request: ReviewRequestDto, feedback: String?) {
    updateRequest(
      request.copy(
        status = "APPROVED",
        feedback = feedback,
      ),
    )
    unblockAgent(request.agentId, "Running after approval")
    appendEvent(
      jobId = request.jobId,
      agentId = request.agentId,
      agentName = request.agentName,
      message = "${request.agentName} received approval for ${request.title}",
    )
    touchJob(request.jobId)
  }

  private fun applyApprovalRejected(request: ReviewRequestDto, feedback: String?) {
    updateRequest(
      request.copy(
        status = "REJECTED",
        feedback = feedback,
      ),
    )
    reviseAgentPlan(
      agentId = request.agentId,
      summary = "Revised plan after rejection: keep legacy/auth/ read-only during migration",
    )
    appendEvent(
      jobId = request.jobId,
      agentId = request.agentId,
      agentName = request.agentName,
      message = "${request.agentName} revised plan after rejection",
    )
    touchJob(request.jobId)
  }

  private fun applyNeedsInputContinued(
    request: ReviewRequestDto,
    selectedOption: String,
    feedback: String?,
  ) {
    updateRequest(
      request.copy(
        status = "INPUT_SELECTED",
        selectedOption = selectedOption,
        feedback = feedback,
      ),
    )
    unblockAgent(request.agentId, "Running with scope: $selectedOption")
    appendEvent(
      jobId = request.jobId,
      agentId = request.agentId,
      agentName = request.agentName,
      message = "${request.agentName} selected test scope: $selectedOption",
    )
    touchJob(request.jobId)
  }

  private fun applyNeedsInputRejected(request: ReviewRequestDto, feedback: String?) {
    updateRequest(
      request.copy(
        status = "REJECTED",
        feedback = feedback,
      ),
    )
    pauseAgent(
      agentId = request.agentId,
      summary = "Paused after input rejected; awaiting new guidance",
    )
    appendEvent(
      jobId = request.jobId,
      agentId = request.agentId,
      agentName = request.agentName,
      message = "${request.agentName} paused after input rejected",
    )
    touchJob(request.jobId)
  }

  private fun updateRequest(updated: ReviewRequestDto) {
    val index = reviewRequests.indexOfFirst { it.id == updated.id }
    if (index >= 0) {
      reviewRequests[index] = updated
    }
  }

  private fun unblockAgent(agentId: String, summary: String) {
    val index = agents.indexOfFirst { it.id == agentId }
    if (index >= 0) {
      val agent = agents[index]
      agents[index] = agent.copy(
        status = "RUNNING",
        currentSummary = summary,
        pendingRequestId = null,
      )
    }
  }

  private fun reviseAgentPlan(agentId: String, summary: String) {
    val index = agents.indexOfFirst { it.id == agentId }
    if (index >= 0) {
      val agent = agents[index]
      agents[index] = agent.copy(
        status = "RUNNING",
        currentSummary = summary,
        pendingRequestId = null,
      )
    }
  }

  private fun pauseAgent(agentId: String, summary: String) {
    val index = agents.indexOfFirst { it.id == agentId }
    if (index >= 0) {
      val agent = agents[index]
      agents[index] = agent.copy(
        status = "IDLE",
        currentSummary = summary,
        pendingRequestId = null,
      )
    }
  }

  private fun appendEvent(
    jobId: String,
    agentId: String,
    agentName: String,
    message: String,
  ) {
    val now = SeedData.BASE_EPOCH_MS + activityEvents.size * SeedData.MINUTE_MS
    activityEvents.add(
      ActivityEventDto(
        id = "event-$nextEventId",
        jobId = jobId,
        agentId = agentId,
        agentName = agentName,
        message = message,
        occurredAtEpochMs = now,
      ),
    )
    nextEventId++
  }

  private fun touchJob(jobId: String) {
    val index = jobs.indexOfFirst { it.id == jobId }
    if (index >= 0) {
      val job = jobs[index]
      jobs[index] = job.copy(
        updatedAtEpochMs = SeedData.BASE_EPOCH_MS + activityEvents.size * SeedData.MINUTE_MS,
      )
    }
  }

  private fun rederiveJobStatuses() {
    jobs.indices.forEach { index ->
      val job = jobs[index]
      val hasPending = reviewRequests.any { it.jobId == job.id && it.status == "PENDING" }
      val newStatus = if (hasPending) {
        "BLOCKED"
      } else {
        val jobAgents = agents.filter { it.jobId == job.id }
        if (jobAgents.isNotEmpty() && jobAgents.all { it.status == "COMPLETED" }) {
          "COMPLETED"
        } else {
          "RUNNING"
        }
      }
      jobs[index] = job.copy(status = newStatus)
    }
  }

  private fun seed() {
    jobs.clear()
    agents.clear()
    reviewRequests.clear()
    activityEvents.clear()
    nextEventId = 100

    jobs.addAll(SeedData.jobs())
    agents.addAll(SeedData.agents())
    reviewRequests.addAll(SeedData.reviewRequests())
    activityEvents.addAll(SeedData.activityEvents())
    rederiveJobStatuses()
  }

  sealed class ResolveResult {
    data object Success : ResolveResult()
    data class HttpError(val code: Int, val message: String) : ResolveResult()
  }
}

private object SeedData {
  const val BASE_EPOCH_MS = 1_700_000_000_000L
  const val MINUTE_MS = 60_000L

  fun jobs(): List<JobDto> = listOf(
    JobDto(
      id = "job-1",
      title = "Migrate auth to OAuth2",
      repoName = "my-app-backend",
      status = "BLOCKED",
      currentStep = 3,
      totalSteps = 5,
      startedAtEpochMs = BASE_EPOCH_MS,
      updatedAtEpochMs = BASE_EPOCH_MS + 3 * MINUTE_MS,
    ),
    JobDto(
      id = "job-2",
      title = "Fix checkout bug",
      repoName = "shop-app",
      status = "RUNNING",
      currentStep = 2,
      totalSteps = 4,
      startedAtEpochMs = BASE_EPOCH_MS + MINUTE_MS,
      updatedAtEpochMs = BASE_EPOCH_MS + 5 * MINUTE_MS,
    ),
    JobDto(
      id = "job-3",
      title = "Add dark mode",
      repoName = "shop-app",
      status = "RUNNING",
      currentStep = 1,
      totalSteps = 3,
      startedAtEpochMs = BASE_EPOCH_MS + 2 * MINUTE_MS,
      updatedAtEpochMs = BASE_EPOCH_MS + 6 * MINUTE_MS,
    ),
  )

  fun agents(): List<AgentDto> = listOf(
    AgentDto(
      id = "agent-architect",
      jobId = "job-1",
      name = "Architect Agent",
      role = "Architect",
      status = "COMPLETED",
      currentSummary = "Completed plan",
      pendingRequestId = null,
    ),
    AgentDto(
      id = "agent-coder",
      jobId = "job-1",
      name = "Coder Agent",
      role = "Coder",
      status = "BLOCKED",
      currentSummary = "Planning auth migration; awaiting delete approval",
      pendingRequestId = "request-coder-approval",
    ),
    AgentDto(
      id = "agent-test",
      jobId = "job-1",
      name = "Test Agent",
      role = "Test",
      status = "BLOCKED",
      currentSummary = "Waiting on test scope selection",
      pendingRequestId = "request-test-input",
    ),
    AgentDto(
      id = "agent-checkout",
      jobId = "job-2",
      name = "Coder Agent",
      role = "Coder",
      status = "RUNNING",
      currentSummary = "Fixing payment validation",
      pendingRequestId = null,
    ),
    AgentDto(
      id = "agent-darkmode",
      jobId = "job-3",
      name = "Coder Agent",
      role = "Coder",
      status = "RUNNING",
      currentSummary = "Implementing theme tokens",
      pendingRequestId = null,
    ),
  )

  fun reviewRequests(): List<ReviewRequestDto> = listOf(
    ReviewRequestDto(
      id = "request-history-approved",
      jobId = "job-1",
      agentId = "agent-architect",
      agentName = "Architect Agent",
      type = "APPROVAL",
      status = "APPROVED",
      title = "Add OAuth callback route",
      risk = "MEDIUM",
      reasoning = "Callback route is required before token exchange can be tested end-to-end.",
      requestedAtEpochMs = BASE_EPOCH_MS - 2 * MINUTE_MS,
      proposedAction = "Add /auth/callback handler in API gateway",
      affectedFiles = listOf("api/routes/auth.ts"),
      question = null,
      options = null,
      selectedOption = null,
      feedback = null,
    ),
    ReviewRequestDto(
      id = "request-coder-approval",
      jobId = "job-1",
      agentId = "agent-coder",
      agentName = "Coder Agent",
      type = "APPROVAL",
      status = "PENDING",
      title = "Delete legacy/auth/",
      risk = "HIGH",
      reasoning = "Legacy auth module is unused after OAuth scaffolding; removing it reduces migration risk.",
      requestedAtEpochMs = BASE_EPOCH_MS + MINUTE_MS,
      proposedAction = "Delete directory legacy/auth/ and remove imports",
      affectedFiles = listOf(
        "legacy/auth/session.ts",
        "legacy/auth/login.ts",
        "legacy/auth/index.ts",
      ),
      question = null,
      options = null,
      selectedOption = null,
      feedback = null,
    ),
    ReviewRequestDto(
      id = "request-test-input",
      jobId = "job-1",
      agentId = "agent-test",
      agentName = "Test Agent",
      type = "NEEDS_INPUT",
      status = "PENDING",
      title = "Choose test scope",
      risk = "MEDIUM",
      reasoning = "Test scope affects runtime and coverage before merge.",
      requestedAtEpochMs = BASE_EPOCH_MS + 2 * MINUTE_MS,
      proposedAction = null,
      affectedFiles = null,
      question = "Which test scope should run before merge?",
      options = listOf("Smoke only", "Critical flows", "Full integration"),
      selectedOption = null,
      feedback = null,
    ),
  )

  fun activityEvents(): List<ActivityEventDto> = listOf(
    ActivityEventDto(
      id = "event-1",
      jobId = "job-1",
      agentId = "agent-architect",
      agentName = "Architect Agent",
      message = "Architect Agent completed migration plan",
      occurredAtEpochMs = BASE_EPOCH_MS - 3 * MINUTE_MS,
    ),
    ActivityEventDto(
      id = "event-2",
      jobId = "job-1",
      agentId = "agent-coder",
      agentName = "Coder Agent",
      message = "Coder Agent requested delete approval for legacy/auth/",
      occurredAtEpochMs = BASE_EPOCH_MS,
    ),
    ActivityEventDto(
      id = "event-3",
      jobId = "job-1",
      agentId = "agent-test",
      agentName = "Test Agent",
      message = "Test Agent needs input on test scope",
      occurredAtEpochMs = BASE_EPOCH_MS + MINUTE_MS,
    ),
  )
}
