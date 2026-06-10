package com.example.aiplaygroundtwo.data.network.fake

import com.example.aiplaygroundtwo.data.network.AgentNetworkApi
import com.example.aiplaygroundtwo.data.network.NetworkResult
import com.example.aiplaygroundtwo.data.network.dto.ReviewResolutionDto
import com.example.aiplaygroundtwo.data.network.dto.SyncPayloadDto
import kotlinx.coroutines.delay

class FakeAgentNetworkApi(
    private val server: FakeAgentServer = FakeAgentServer(),
    private val simulatedDelayMs: Long = 300L,
) : AgentNetworkApi {
    enum class FailureMode {
        NONE,
        NETWORK_ERROR,
        SERVER_500,
        VALIDATION_400,
        CONFLICT_409,
    }

    var failureMode: FailureMode = FailureMode.NONE

    override suspend fun getSnapshot(): NetworkResult<SyncPayloadDto> {
        val failure = consumeFailureIfArmed()
        if (failure != null) return failure
        delay(simulatedDelayMs)
        return NetworkResult.Success(server.buildSnapshot())
    }

    override suspend fun submitReviewResolution(
        resolution: ReviewResolutionDto,
    ): NetworkResult<SyncPayloadDto> {
        val failure = consumeFailureIfArmed()
        if (failure != null) return failure
        delay(simulatedDelayMs)
        return when (val result = server.resolve(resolution)) {
            is FakeAgentServer.ResolveResult.Success ->
                NetworkResult.Success(server.buildSnapshot())
            is FakeAgentServer.ResolveResult.HttpError ->
                NetworkResult.HttpError(result.code, result.message)
        }
    }

    fun server(): FakeAgentServer = server

    private fun consumeFailureIfArmed(): NetworkResult<SyncPayloadDto>? {
        val mode = failureMode
        if (mode == FailureMode.NONE) return null
        failureMode = FailureMode.NONE
        return when (mode) {
            FailureMode.NETWORK_ERROR ->
                NetworkResult.NetworkError(IllegalStateException("Simulated network failure"))
            FailureMode.SERVER_500 ->
                NetworkResult.HttpError(500, "Simulated server error")
            FailureMode.VALIDATION_400 ->
                NetworkResult.HttpError(400, "Simulated validation error")
            FailureMode.CONFLICT_409 ->
                NetworkResult.HttpError(409, "Request already resolved")
            FailureMode.NONE -> null
        }
    }
}
