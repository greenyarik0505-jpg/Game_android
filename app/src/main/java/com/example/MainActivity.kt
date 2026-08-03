package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.NomadViewModel
import com.example.ui.ScreenState
import com.example.ui.screens.*
import com.example.ui.theme.DeepSpaceBackground
import com.example.ui.theme.GravityNomadTheme

class MainActivity : ComponentActivity() {
    private val viewModel: NomadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GravityNomadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepSpaceBackground
                ) {
                    GravityNomadApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun GravityNomadApp(viewModel: NomadViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    when (currentScreen) {
        ScreenState.MENU -> MainMenuScreen(viewModel)
        ScreenState.PLAY -> GamePlayScreen(viewModel)
        ScreenState.GARAGE -> GarageScreen(viewModel)
        ScreenState.TECH_TREE -> TechTreeScreen(viewModel)
        ScreenState.QUESTS -> QuestsScreen(viewModel)
        ScreenState.ROGUELIKE_DRAFT -> RoguelikeDraftDialog(viewModel)
        ScreenState.SUMMARY -> SummaryScreen(viewModel)
        ScreenState.SANDBOX -> MainMenuScreen(viewModel)
    }
}
