package com.example.aiplaygroundtwo.di

import android.content.Context
import androidx.room.Room
import com.example.aiplaygroundtwo.data.local.AgentDatabase
import com.example.aiplaygroundtwo.data.network.AgentNetworkApi
import com.example.aiplaygroundtwo.data.network.fake.FakeAgentNetworkApi
import com.example.aiplaygroundtwo.data.repository.AgentRepository
import com.example.aiplaygroundtwo.data.repository.DefaultAgentRepository

class AppContainer(context: Context) {
    val dispatchers: DispatcherProvider = DefaultDispatcherProvider()

    val database: AgentDatabase = Room.databaseBuilder(
        context,
        AgentDatabase::class.java,
        "agent_control.db",
    ).build()

    val fakeNetworkApi: FakeAgentNetworkApi = FakeAgentNetworkApi()

    val networkApi: AgentNetworkApi = fakeNetworkApi

    val repository: AgentRepository = DefaultAgentRepository(
        database = database,
        networkApi = networkApi,
        dispatchers = dispatchers,
    )
}
