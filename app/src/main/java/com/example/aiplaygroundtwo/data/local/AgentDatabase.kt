package com.example.aiplaygroundtwo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.aiplaygroundtwo.data.local.dao.ActivityEventDao
import com.example.aiplaygroundtwo.data.local.dao.AgentDao
import com.example.aiplaygroundtwo.data.local.dao.JobDao
import com.example.aiplaygroundtwo.data.local.dao.ReviewRequestDao
import com.example.aiplaygroundtwo.data.local.entity.ActivityEventEntity
import com.example.aiplaygroundtwo.data.local.entity.AgentEntity
import com.example.aiplaygroundtwo.data.local.entity.JobEntity
import com.example.aiplaygroundtwo.data.local.entity.ReviewRequestEntity

@Database(
    entities = [
        JobEntity::class,
        AgentEntity::class,
        ReviewRequestEntity::class,
        ActivityEventEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AgentDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
    abstract fun agentDao(): AgentDao
    abstract fun reviewRequestDao(): ReviewRequestDao
    abstract fun activityEventDao(): ActivityEventDao
}
