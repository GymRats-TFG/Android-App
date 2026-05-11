package com.gymrats.gymratsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gymrats.gymratsapp.navigation.AppNavigation
import com.gymrats.gymratsapp.ui.theme.GymRatsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GymRatsTheme {
                AppNavigation()
            }
        }
    }
}