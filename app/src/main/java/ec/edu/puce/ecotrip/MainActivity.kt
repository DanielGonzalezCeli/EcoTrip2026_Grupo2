package ec.edu.puce.ecotrip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ec.edu.puce.ecotrip.navigation.EcoTripNavHost
import ec.edu.puce.ecotrip.ui.theme.EcoTripTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoTripTheme {
                EcoTripNavHost()
            }
        }
    }
}