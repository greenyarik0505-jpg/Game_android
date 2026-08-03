package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.BiomeType
import com.example.engine.GameMode
import com.example.ui.NomadViewModel
import com.example.ui.ScreenState
import com.example.ui.theme.*

@Composable
fun MainMenuScreen(
    viewModel: NomadViewModel
) {
    val profile by viewModel.playerProfile.collectAsState()
    val selectedBiome by viewModel.selectedBiome.collectAsState()
    val selectedMode by viewModel.selectedGameMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Currency Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GRAVITY NOMAD",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonCyan,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Научно-фантастическая физическая экспедиция",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Crystals
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceCard,
                    border = BorderStroke(1.dp, NeonCyan)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Diamond, contentDescription = "Кристаллы", tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Text(text = "${profile?.crystals ?: 0}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                // Alloy
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceCard,
                    border = BorderStroke(1.dp, NeonPink)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.PrecisionManufacturing, contentDescription = "Сплавы", tint = NeonPink, modifier = Modifier.size(16.dp))
                        Text(text = "${profile?.alloy ?: 0}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                // Tech Points
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceCard,
                    border = BorderStroke(1.dp, GravRingViolet)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Memory, contentDescription = "Очки Технологий", tint = GravRingViolet, modifier = Modifier.size(16.dp))
                        Text(text = "${profile?.techPoints ?: 0}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Season Banner
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = DarkSurfaceCard,
            border = BorderStroke(1.dp, NeonAmber),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Star, contentDescription = "Season", tint = NeonAmber, modifier = Modifier.size(20.dp))
                    Text(text = "СЕЗОН 1: ГРАВИТАЦИОННЫЙ ШТОРМ", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = NeonAmber)
                }
                Text(text = "ОСТАЛОСЬ 14Д", fontSize = 10.sp, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Launch Drive Hero Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(selectedBiome.primaryColor, DarkSurfaceCard)
                    )
                )
                .border(2.dp, NeonCyan, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = "ЭКСПЕДИЦИЯ ГОТОВА к СТАРТУ",
                    color = NeonAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = selectedBiome.title,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Режим: ${selectedMode.title}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = { viewModel.startRun(selectedBiome, selectedMode) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .testTag("launch_expedition_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Старт", tint = DeepSpaceBackground)
                    Text("В ПУТЬ", color = DeepSpaceBackground, fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Biome Selector Section
        Text(
            text = "ВЫБЕРИТЕ МИР ДЛЯ ЭКСПЕДИЦИИ",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(BiomeType.values()) { biome ->
                val isSelected = selectedBiome == biome
                Card(
                    onClick = { viewModel.selectedBiome.value = biome },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) biome.primaryColor.copy(alpha = 0.4f) else DarkSurfaceCard
                    ),
                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) NeonCyan else DarkSurfaceCardBorder),
                    modifier = Modifier.width(150.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(biome.primaryColor)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = biome.title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = biome.description, fontSize = 10.sp, color = TextSecondary, maxLines = 2)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Game Mode Selector
        Text(
            text = "РЕЖИМЫ ИГРЫ",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = NeonPink,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            GameMode.values().forEach { mode ->
                val isSelected = selectedMode == mode
                Surface(
                    onClick = { viewModel.selectedGameMode.value = mode },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) DarkSurfaceCardBorder else DarkSurfaceCard,
                    border = BorderStroke(if (isSelected) 1.5.dp else 0.5.dp, if (isSelected) NeonPink else DarkSurfaceCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (mode) {
                                GameMode.INFINITE -> Icons.Default.AllInclusive
                                GameMode.ROGUELIKE -> Icons.Default.Casino
                                GameMode.CHALLENGES -> Icons.Default.Flag
                                GameMode.BOSS_BATTLE -> Icons.Default.Adb
                                GameMode.SANDBOX -> Icons.Default.Build
                            },
                            contentDescription = mode.title,
                            tint = if (isSelected) NeonPink else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = mode.title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                            Text(text = mode.description, fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Bottom Navigation Quick Links
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavIconButton(
                icon = Icons.Default.Build,
                label = "ГАРАЖ",
                tint = NeonCyan,
                onClick = { viewModel.currentScreen.value = ScreenState.GARAGE },
                testTag = "nav_garage_button"
            )
            NavIconButton(
                icon = Icons.Default.Memory,
                label = "ТЕХНОЛОГИИ",
                tint = GravRingViolet,
                onClick = { viewModel.currentScreen.value = ScreenState.TECH_TREE },
                testTag = "nav_tech_button"
            )
            NavIconButton(
                icon = Icons.Default.Task,
                label = "ЗАДАНИЯ",
                tint = NeonAmber,
                onClick = { viewModel.currentScreen.value = ScreenState.QUESTS },
                testTag = "nav_quests_button"
            )
        }
    }
}

@Composable
fun NavIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(DarkSurfaceCard)
                .border(1.dp, tint, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = tint)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}
