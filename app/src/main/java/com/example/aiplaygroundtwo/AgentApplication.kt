package com.example.aiplaygroundtwo

import android.app.Application
import com.example.aiplaygroundtwo.di.AppContainer

class AgentApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
