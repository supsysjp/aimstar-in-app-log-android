package com.your.company

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.your.company.ui.theme.AimstarInAppLogTheme
import jp.co.aimstar.logging.android.AimstarInAppLog
import jp.co.aimstar.logging.android.AimstarLogSDKConfig
import jp.co.aimstar.logging.android.data.model.CustomValueType

class MainActivity : ComponentActivity() {
    private val apiKey = "YOUR API KEY"
    private val tenantId = "YOUR TENANT ID"
    private val customerId = "test_user_001"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val config = AimstarLogSDKConfig(apiKey = apiKey, tenantId = tenantId)
        AimstarInAppLog.setup(context = this.applicationContext, config = config)

        AimstarInAppLog.updateLoginState(customerId)

        setContent {
            AimstarInAppLogTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        customerId = customerId,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(customerId: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            AimstarInAppLog.trackPageView(
                pageTitle = "PageViewScreen",
                pageUrl = "https://page/pageview",
                referrerUrl = "https://page/home",
                customParams = mapOf(
                    "is_logged_in" to CustomValueType.BooleanValue(customerId.isNotEmpty()),
                    "membership_level" to if (customerId.isNotEmpty()) {
                        CustomValueType.StringValue("gold")
                    } else {
                        CustomValueType.StringValue("guest")
                    }
                )
            )
        }) {
            Text(text = "send page viewed event")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AimstarInAppLogTheme {
        Greeting("test_user_001")
    }
}