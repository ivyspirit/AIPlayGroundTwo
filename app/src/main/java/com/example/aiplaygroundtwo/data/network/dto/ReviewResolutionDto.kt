package com.example.aiplaygroundtwo.data.network.dto

data class ReviewResolutionDto(
    val requestId: String,
    val action: String,
    val feedback: String? = null,
    val selectedOption: String? = null,
)
