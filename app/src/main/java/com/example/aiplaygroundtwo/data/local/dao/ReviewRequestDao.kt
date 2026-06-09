package com.example.aiplaygroundtwo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aiplaygroundtwo.data.local.entity.ReviewRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewRequestDao {
    @Query("SELECT * FROM review_requests WHERE jobId = :jobId AND status = 'PENDING' ORDER BY requestedAtEpochMs ASC")
    fun observePendingForJob(jobId: String): Flow<List<ReviewRequestEntity>>

    @Query("SELECT * FROM review_requests WHERE status = 'PENDING' ORDER BY requestedAtEpochMs ASC")
    fun observeAllPending(): Flow<List<ReviewRequestEntity>>

    @Query("SELECT * FROM review_requests WHERE status != 'PENDING' ORDER BY requestedAtEpochMs DESC")
    fun observeHistory(): Flow<List<ReviewRequestEntity>>

    @Query("SELECT * FROM review_requests WHERE id = :requestId")
    fun observeById(requestId: String): Flow<ReviewRequestEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(requests: List<ReviewRequestEntity>)

    @Query("DELETE FROM review_requests")
    suspend fun deleteAll()
}
