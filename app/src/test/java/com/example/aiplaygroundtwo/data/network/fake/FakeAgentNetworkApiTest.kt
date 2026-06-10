package com.example.aiplaygroundtwo.data.network.fake

import com.example.aiplaygroundtwo.data.network.NetworkResult
import com.example.aiplaygroundtwo.data.network.dto.ReviewResolutionDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeAgentNetworkApiTest {
    private lateinit var api: FakeAgentNetworkApi

    @Before
    fun setUp() {
        api = FakeAgentNetworkApi(simulatedDelayMs = 0L)
    }

    @Test
    fun networkError_returnsNetworkResultAndResetsMode() = runTest {
        api.failureMode = FakeAgentNetworkApi.FailureMode.NETWORK_ERROR
        val result = api.getSnapshot()
        assertTrue(result is NetworkResult.NetworkError)
        assertEquals(FakeAgentNetworkApi.FailureMode.NONE, api.failureMode)
    }

    @Test
    fun server500_returnsHttpErrorAndResetsMode() = runTest {
        api.failureMode = FakeAgentNetworkApi.FailureMode.SERVER_500
        val result = api.getSnapshot()
        assertTrue(result is NetworkResult.HttpError)
        assertEquals(500, (result as NetworkResult.HttpError).code)
        assertEquals(FakeAgentNetworkApi.FailureMode.NONE, api.failureMode)
    }

    @Test
    fun validation400_returnsHttpErrorAndResetsMode() = runTest {
        api.failureMode = FakeAgentNetworkApi.FailureMode.VALIDATION_400
        val result = api.submitReviewResolution(
            ReviewResolutionDto(
                requestId = "request-coder-approval",
                action = "APPROVE",
                feedback = null,
                selectedOption = null,
            ),
        )
        assertTrue(result is NetworkResult.HttpError)
        assertEquals(400, (result as NetworkResult.HttpError).code)
        assertEquals(FakeAgentNetworkApi.FailureMode.NONE, api.failureMode)
    }

    @Test
    fun conflict409_returnsHttpErrorAndResetsMode() = runTest {
        api.failureMode = FakeAgentNetworkApi.FailureMode.CONFLICT_409
        val result = api.submitReviewResolution(
            ReviewResolutionDto(
                requestId = "request-coder-approval",
                action = "APPROVE",
                feedback = null,
                selectedOption = null,
            ),
        )
        assertTrue(result is NetworkResult.HttpError)
        assertEquals(409, (result as NetworkResult.HttpError).code)
        assertEquals(FakeAgentNetworkApi.FailureMode.NONE, api.failureMode)
    }
}
