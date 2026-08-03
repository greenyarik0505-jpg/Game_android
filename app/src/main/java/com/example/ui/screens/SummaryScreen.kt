package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NomadViewModel
import com.example.ui.ScreenState
import com.example.ui.theme.*

@Composable
fun SummaryScreen(
    viewModel: NomadViewModel
) {
    val stats by viewModel.liveRunStats.collectAsState()
    val biome by viewModel.selectedBiome.collectAsState()
    val mode by viewModel.selectedGameMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBackground)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("ИТОГИ ЭКСПЕДИЦИИ", fontSize = 28.sp, fontWeight = FontWeight.Black, color = OverheatCrimson, letterSpacing = 2.sp)
        Text(stats.gameOverReason, fontSize = 14.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            border = BorderStroke(1.5.dp, NeonCyan),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ПРОЙДЕННАЯ ДИСТАНЦИЯ", fontSize = 12.sp, color = TextSecondary)
                    Text("${stats.distanceMeters.toInt()} метров", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("СОБРАНО КРИСТАЛЛОВ", fontSize = 12.sp, color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Diamond, contentDescription = "Кристаллы", tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Text("+${stats.crystalsCollected}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ДОБЫТО СПЛАВОВ", fontSize = 12.sp, color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.PrecisionManufacturing, contentDescription = "Сплавы", tint = NeonPink, modifier = Modifier.size(16.dp))
                        Text("+${stats.alloyCollected}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ТРЮКОВ В ВОЗДУХЕ", fontSize = 12.sp, color = TextSecondary)
                    Text("${stats.flipsDone}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonAmber)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = { viewModel.currentScreen.value = ScreenState.GARAGE },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, NeonCyan),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("summary_garage_button")
            ) {
                Icon(Icons.Default.Build, contentDescription = "Гараж", tint = NeonCyan)
                Spacer(modifier = Modifier.width(6.dp))
                Text("ГАРАЖ", color = NeonCyan, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.startRun(biome, mode) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("summary_retry_button")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Повторить", tint = DeepSpaceBackground)
                Spacer(modifier = Modifier.width(6.dp))
                Text("ПОВТОРИТЬ", color = DeepSpaceBackground, fontWeight = FontWeight.Black)
            }
        }
    }
}
