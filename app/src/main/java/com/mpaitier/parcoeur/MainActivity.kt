package com.mpaitier.parcoeur

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mpaitier.parcoeur.core.theme.ParCoeurTheme
import com.mpaitier.parcoeur.navigation.AppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParCoeurTheme {
                AppNavHost()
            }
        }
    }
}
