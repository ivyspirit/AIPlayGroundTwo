package com.example.aiplaygroundtwo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.aiplaygroundtwo.ui.AgentApp
import com.example.aiplaygroundtwo.ui.theme.AIPlayGroundTwoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as AgentApplication).appContainer
        setContent {
            AIPlayGroundTwoTheme {
                AgentApp(
                    repository = appContainer.repository,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
