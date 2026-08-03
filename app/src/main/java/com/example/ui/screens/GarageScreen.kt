package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.DefaultModulesCatalog
import com.example.engine.ModuleCategory
import com.example.engine.VehicleModule
import com.example.ui.NomadViewModel
import com.example.ui.ScreenState
import com.example.ui.theme.*

@Composable
fun GarageScreen(
    viewModel: NomadViewModel
) {
    val unlockedModules by viewModel.unlockedModules.collectAsState()
    val profile by viewModel.playerProfile.collectAsState()

    val equippedChassisId by viewModel.equippedChassisId.collectAsState()
    val equippedEngineId by viewModel.equippedEngineId.collectAsState()
    val equippedWheelsId by viewModel.equippedWheelsId.collectAsState()
    val equippedCoreId by viewModel.equippedCoreId.collectAsState()
    val equippedStabilizerId by viewModel.equippedStabilizerId.collectAsState()
    val equippedUtilityId by viewModel.equippedUtilityId.collectAsState()

    var selectedCategory by remember { mutableStateOf(ModuleCategory.CHASSIS) }

    val equippedMap = mapOf(
        ModuleCategory.CHASSIS to DefaultModulesCatalog.getModuleById(equippedChassisId),
        ModuleCategory.ENGINE to DefaultModulesCatalog.getModuleById(equippedEngineId),
        ModuleCategory.WHEELS to DefaultModulesCatalog.getModuleById(equippedWheelsId),
        ModuleCategory.CORE to DefaultModulesCatalog.getModuleById(equippedCoreId),
        ModuleCategory.STABILIZER to DefaultModulesCatalog.getModuleById(equippedStabilizerId),
        ModuleCategory.UTILITY to DefaultModulesCatalog.getModuleById(equippedUtilityId)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBackground)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { viewModel.currentScreen.value = ScreenState.MENU },
                    modifier = Modifier.testTag("garage_back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NeonCyan)
                }
                Text("МОДУЛЬНАЯ МАСТЕРСКАЯ", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceCard,
                border = BorderStroke(1.dp, NeonCyan)
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Icon(Icons.Default.Diamond, contentDescription = "Кристаллы", tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${profile?.crystals ?: 0}", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Equipped Module Slots Summary
        Text("УСТАНОВЛЕННЫЕ КОМПОНЕНТЫ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(ModuleCategory.values()) { category ->
                val module = equippedMap[category]!!
                val isSelected = selectedCategory == category

                Card(
                    onClick = { selectedCategory = category },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) DarkSurfaceCardBorder else DarkSurfaceCard
                    ),
                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) NeonCyan else DarkSurfaceCardBorder),
                    modifier = Modifier.width(130.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(category.displayName, fontSize = 10.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(module.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = module.rarity.color, maxLines = 1)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Category Filter Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedCategory.ordinal,
            containerColor = DeepSpaceBackground,
            contentColor = NeonCyan,
            edgePadding = 0.dp
        ) {
            ModuleCategory.values().forEach { category ->
                Tab(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    text = { Text(category.displayName, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Catalog Parts List for Category
        val catalogForCategory = DefaultModulesCatalog.ALL_CATALOG_MODULES.filter { it.category == selectedCategory }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(catalogForCategory) { module ->
                val isUnlocked = unlockedModules.any { it.moduleId == module.id }
                val isEquipped = equippedMap[selectedCategory]?.id == module.id

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    border = BorderStroke(if (isEquipped) 2.dp else 1.dp, if (isEquipped) NeonPink else module.rarity.color.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(module.rarity.color.copy(alpha = 0.2f))
                                .border(1.dp, module.rarity.color, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Extension, contentDescription = module.name, tint = module.rarity.color)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(module.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = module.rarity.color.copy(alpha = 0.2f)
                                ) {
                                    Text(module.rarity.displayName, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = module.rarity.color, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(module.description, fontSize = 11.sp, color = TextSecondary)
                            if (module.specialPerk.isNotEmpty()) {
                                Text("Эффект: ${module.specialPerk}", fontSize = 10.sp, color = NeonAmber, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (isEquipped) {
                            Button(
                                onClick = {},
                                enabled = false,
                                colors = ButtonDefaults.buttonColors(disabledContainerColor = DarkSurfaceCardBorder)
                            ) {
                                Text("АКТИВНО", fontSize = 11.sp, color = NeonPink)
                            }
                        } else if (isUnlocked) {
                            Button(
                                onClick = { viewModel.equipModule(selectedCategory, module.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                            ) {
                                Text("НАДЕТЬ", fontSize = 11.sp, color = DeepSpaceBackground, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            val unlockCost = when (module.rarity) {
                                com.example.engine.ModuleRarity.COMMON -> 100
                                com.example.engine.ModuleRarity.RARE -> 250
                                com.example.engine.ModuleRarity.EPIC -> 500
                                com.example.engine.ModuleRarity.LEGENDARY -> 1000
                            }
                            Button(
                                onClick = { viewModel.unlockModule(module.id, unlockCost) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonAmber)
                            ) {
                                Text("$unlockCost 💎", fontSize = 11.sp, color = DeepSpaceBackground, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
