package com.example.aiplaygroundtwo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.aiplaygroundtwo.ui.placeholder.PlaceholderScreen
import com.example.aiplaygroundtwo.ui.theme.AIPlayGroundTwoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIPlayGroundTwoTheme {
                PlaceholderScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
