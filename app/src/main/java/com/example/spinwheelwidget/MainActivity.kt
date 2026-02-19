package com.example.spinwheelwidget

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.spinwheel.SpinWheelSdk
import com.example.spinwheel.config.SpinWheelConstants
import com.example.spinwheel.data.SpinWheelCacheManager
import com.example.spinwheelwidget.ui.theme.SpinWheelWidgetTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var uiState by mutableStateOf("Initializing...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        refreshAssets()

        setContent {
            SpinWheelWidgetTheme {
                StatusScreen(uiState)
            }
        }
    }

    private fun refreshAssets() {
        lifecycleScope.launch {
            try {
                uiState = "Refreshing..."

                SpinWheelSdk.refreshIfNeeded(
                    applicationContext,
                    SpinWheelConstants.CONFIG_URL
                )

                val cache = SpinWheelCacheManager(applicationContext)

                val ready =
                    cache.getAssetFile("bg.jpeg").exists() &&
                            cache.getAssetFile("wheel.png").exists() &&
                            cache.getAssetFile("wheel-frame.png").exists() &&
                            cache.getAssetFile("wheel-spin.png").exists() &&
                            cache.framesCount() >= 12

                uiState = if (ready) {
                    "Widget Ready\nAll assets downloaded\nFrames generated"
                } else {
                    "Assets missing or frames not ready"
                }

            } catch (t: Throwable) {
                Log.e("SPIN_DEBUG", "Warmup failed", t)
                uiState = "Error: ${t.message}"
            }
        }
    }

    @Composable
    fun StatusScreen(status: String) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "SpinWheel Demo",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = status,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}