package com.example.aiplaygroundtwo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "activity_events",
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
    indices = [Index("jobId"), Index("agentId")],
)
data class ActivityEventEntity(
    @PrimaryKey val id: String,
    val jobId: String,
    val agentId: String,
    val agentName: String,
    val message: String,
    val occurredAtEpochMs: Long,
)
