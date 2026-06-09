package com.example.aiplaygroundtwo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "review_requests",
    foreignKeys = [
        ForeignKey(
            entity = JobEntity::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = AgentEntity::class,
            parentColumns = ["id"],
            childColumns = ["agentId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("jobId"),
        Index("agentId"),
        Index("status"),
        Index("type"),
    ],
)
data class ReviewRequestEntity(
    @PrimaryKey val id: String,
    val jobId: String,
    val agentId: String,
    val agentName: String,
    val type: String,
    val status: String,
    val title: String,
    val risk: String,
    val reasoning: String,
    val requestedAtEpochMs: Long,
    val proposedAction: String?,
    val affectedFiles: List<String>?,
    val question: String?,
    val options: List<String>?,
    val selectedOption: String?,
    val feedback: String?,
)
