package com.example.aiplaygroundtwo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aiplaygroundtwo.data.local.entity.AgentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentDao {
    @Query("SELECT * FROM agents WHERE jobId = :jobId ORDER BY name ASC")
    fun observeForJob(jobId: String): Flow<List<AgentEntity>>

    @Query("SELECT * FROM agents WHERE id = :agentId")
    fun observeById(agentId: String): Flow<AgentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(agents: List<AgentEntity>)

    @Query("DELETE FROM agents")
    suspend fun deleteAll()
}
