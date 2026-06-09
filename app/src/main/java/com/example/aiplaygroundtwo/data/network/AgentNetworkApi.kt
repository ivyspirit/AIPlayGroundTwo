package com.example.aiplaygroundtwo.data.network

import com.example.aiplaygroundtwo.data.network.dto.ReviewResolutionDto
import com.example.aiplaygroundtwo.data.network.dto.SyncPayloadDto

interface AgentNetworkApi {
    suspend fun getSnapshot(): NetworkResult<SyncPayloadDto>
    suspend fun submitReviewResolution(resolution: ReviewResolutionDto): NetworkResult<SyncPayloadDto>
}
