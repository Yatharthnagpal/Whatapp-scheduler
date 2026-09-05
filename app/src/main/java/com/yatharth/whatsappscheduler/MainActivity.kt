package com.yatharth.whatsappscheduler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.yatharth.whatsappscheduler.ui.navigation.NavGraph
import com.yatharth.whatsappscheduler.ui.theme.WhatsAppSchedulerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhatsAppSchedulerTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
