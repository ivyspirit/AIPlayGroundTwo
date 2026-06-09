package com.example.aiplaygroundtwo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey val id: String,
    val title: String,
    val repoName: String,
    val status: String,
    val currentStep: Int,
    val totalSteps: Int,
    val startedAtEpochMs: Long,
    val updatedAtEpochMs: Long,
)
