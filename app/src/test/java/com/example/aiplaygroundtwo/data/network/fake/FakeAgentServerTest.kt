package com.example.aiplaygroundtwo.data.network.fake

import com.example.aiplaygroundtwo.data.network.dto.ReviewResolutionDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeAgentServerTest {
    private lateinit var server: FakeAgentServer

    @Before
    fun setUp() {
        server = FakeAgentServer()
    }

    @Test
    fun seed_containsBothRequestTypes_andPendingRequests() {
        val snapshot = server.buildSnapshot()
        val pending = snapshot.reviewRequests.filter { it.status == "PENDING" }
        assertEquals(2, pending.size)
        assertTrue(pending.any { it.type == "APPROVAL" })
        assertTrue(pending.any { it.type == "NEEDS_INPUT" })
    }

    @Test
    fun approve_updatesRequestAndUnblocksAgent_jobStaysBlockedWithOtherPending() {
        val result = server.resolve(
            ReviewResolutionDto(
                requestId = "request-coder-approval",
                action = "APPROVE",
            ),
        )
        assertTrue(result is FakeAgentServer.ResolveResult.Success)

        val snapshot = server.buildSnapshot()
        val request = snapshot.reviewRequests.first { it.id == "request-coder-approval" }
        val coder = snapshot.agents.first { it.id == "agent-coder" }
        val job = snapshot.jobs.first { it.id == "job-1" }

        assertEquals("APPROVED", request.status)
        assertEquals("RUNNING", coder.status)
        assertEquals(null, coder.pendingRequestId)
        assertEquals("BLOCKED", job.status)
        assertTrue(snapshot.activityEvents.any { it.message.contains("received approval") })
    }

    @Test
    fun reject_revisesAgentPlan_andAppendsActivity() {
        val result = server.resolve(
            ReviewResolutionDto(
                requestId = "request-coder-approval",
                action = "REJECT",
                feedback = "Keep legacy code for now",
            ),
        )
        assertTrue(result is FakeAgentServer.ResolveResult.Success)

        val snapshot = server.buildSnapshot()
        val request = snapshot.reviewRequests.first { it.id == "request-coder-approval" }
        val coder = snapshot.agents.first { it.id == "agent-coder" }

        assertEquals("REJECTED", request.status)
        assertEquals("Keep legacy code for now", request.feedback)
        assertTrue(coder.currentSummary.contains("Revised plan"))
        assertTrue(snapshot.activityEvents.any { it.message.contains("revised plan") })
    }

    @Test
    fun continueRequiresOption_rejectsWithoutSelection() {
        val result = server.resolve(
            ReviewResolutionDto(
                requestId = "request-test-input",
                action = "CONTINUE",
                selectedOption = null,
            ),
        )
        assertTrue(result is FakeAgentServer.ResolveResult.HttpError)
        assertEquals(400, (result as FakeAgentServer.ResolveResult.HttpError).code)
    }

    @Test
    fun blockedDerivation_jobBlockedIffPendingRequestsRemain() {
        val initial = server.buildSnapshot()
        assertEquals("BLOCKED", initial.jobs.first { it.id == "job-1" }.status)

        server.resolve(
            ReviewResolutionDto(
                requestId = "request-coder-approval",
                action = "APPROVE",
            ),
        )
        assertEquals(
            "BLOCKED",
            server.buildSnapshot().jobs.first { it.id == "job-1" }.status,
        )

        server.resolve(
            ReviewResolutionDto(
                requestId = "request-test-input",
                action = "CONTINUE",
                selectedOption = "Critical flows",
            ),
        )
        assertEquals(
            "RUNNING",
            server.buildSnapshot().jobs.first { it.id == "job-1" }.status,
        )
    }
}
