package com.example.aiplaygroundtwo.data.network.fake

import com.example.aiplaygroundtwo.data.network.AgentNetworkApi
import com.example.aiplaygroundtwo.data.network.NetworkResult
import com.example.aiplaygroundtwo.data.network.dto.ReviewResolutionDto
import com.example.aiplaygroundtwo.data.network.dto.SyncPayloadDto
import kotlinx.coroutines.delay

class FakeAgentNetworkApi(
  private val server: FakeAgentServer = FakeAgentServer(),
  private val simulatedDelayMs: Long = 300L,
  var failNextCall: Boolean = false,
) : AgentNetworkApi {
  override suspend fun getSnapshot(): NetworkResult<SyncPayloadDto> {
    if (failNextCall) {
      failNextCall = false
      return NetworkResult.NetworkError(IllegalStateException("Simulated network failure"))
    }
    delay(simulatedDelayMs)
    return NetworkResult.Success(server.buildSnapshot())
  }

  override suspend fun submitReviewResolution(
    resolution: ReviewResolutionDto,
  ): NetworkResult<SyncPayloadDto> {
    if (failNextCall) {
      failNextCall = false
      return NetworkResult.NetworkError(IllegalStateException("Simulated network failure"))
    }
    delay(simulatedDelayMs)
    return when (val result = server.resolve(resolution)) {
      is FakeAgentServer.ResolveResult.Success ->
        NetworkResult.Success(server.buildSnapshot())
      is FakeAgentServer.ResolveResult.HttpError ->
        NetworkResult.HttpError(result.code, result.message)
    }
  }

  fun server(): FakeAgentServer = server
}
