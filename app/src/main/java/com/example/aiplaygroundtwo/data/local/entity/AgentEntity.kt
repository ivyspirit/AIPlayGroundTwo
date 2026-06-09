package com.example.aiplaygroundtwo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "agents",
    foreignKeys = [
        ForeignKey(
            entity = JobEntity::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("jobId")],
)
data class AgentEntity(
    @PrimaryKey val id: String,
    val jobId: String,
    val name: String,
    val role: String,
    val status: String,
    val currentSummary: String,
    val pendingRequestId: String?,
)
