package com.example.aiplaygroundtwo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aiplaygroundtwo.data.local.entity.ActivityEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityEventDao {
    @Query("SELECT * FROM activity_events WHERE jobId = :jobId ORDER BY occurredAtEpochMs DESC")
    fun observeForJob(jobId: String): Flow<List<ActivityEventEntity>>

    @Query(
        "SELECT * FROM activity_events WHERE jobId = :jobId AND agentId = :agentId " +
            "ORDER BY occurredAtEpochMs DESC LIMIT :limit",
    )
    fun observeForAgent(jobId: String, agentId: String, limit: Int): Flow<List<ActivityEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(events: List<ActivityEventEntity>)

    @Query("DELETE FROM activity_events")
    suspend fun deleteAll()
}
