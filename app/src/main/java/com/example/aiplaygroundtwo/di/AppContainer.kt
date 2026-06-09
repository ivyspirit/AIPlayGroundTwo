package com.example.aiplaygroundtwo.di

import android.content.Context
import androidx.room.Room
import com.example.aiplaygroundtwo.data.local.AgentDatabase

class AppContainer(context: Context) {
    val database: AgentDatabase = Room.databaseBuilder(
        context,
        AgentDatabase::class.java,
        "agent_control.db",
    ).build()
}
