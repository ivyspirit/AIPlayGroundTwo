package com.example.aiplaygroundtwo.domain.model

data class ReviewResolution(
    val requestId: String,
    val action: ResolutionAction,
    val feedback: String? = null,
    val selectedOption: String? = null,
)
