package com.example.spinwheelwidget

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.spinwheel.SpinWheelSdk
import com.example.spinwheel.config.SpinWheelConstants
import com.example.spinwheelwidget.ui.theme.SpinWheelWidgetTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            try {
                Log.d("SPIN_DEBUG", "App warmup refresh started")
                SpinWheelSdk.refreshIfNeeded(applicationContext, SpinWheelConstants.CONFIG_URL)
                Log.d("SPIN_DEBUG", "App warmup refresh finished")
            } catch (t: Throwable) {
                Log.e("SPIN_DEBUG", "App warmup refresh failed", t)
            }
        }

        setContent {
            SpinWheelWidgetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "SpinWheel Widget Demo",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SpinWheelWidgetTheme {
        Greeting("Android")
    }
}