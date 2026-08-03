package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NomadViewModel
import com.example.ui.ScreenState
import com.example.ui.theme.*

data class TechSkill(
    val id: String,
    val title: String,
    val description: String,
    val costTechPoints: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun TechTreeScreen(
    viewModel: NomadViewModel
) {
    val profile by viewModel.playerProfile.collectAsState()

    val skills = listOf(
        TechSkill("tech_grav_cap", "Грави-Емкость", "+25% к максимальной емкости энергии.", 2, Icons.Default.BatteryChargingFull),
        TechSkill("tech_nanite", "Нано-Ремонт", "Пассивное восстановление корпуса +2 ХП/сек.", 3, Icons.Default.HealthAndSafety),
        TechSkill("tech_thermal", "Термо-Эффективность", "Снижает нагрев двигателей на 30%.", 3, Icons.Default.Thermostat),
        TechSkill("tech_quantum_drive", "Квантовый Двигатель", "+20% к максимальной скорости во всех биомах.", 4, Icons.Default.Speed),
        TechSkill("tech_magnet_field", "Магнитное Аура-Поле", "Автоматически притягивает ближайшие кристаллы.", 5, Icons.Default.Waves)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBackground)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { viewModel.currentScreen.value = ScreenState.MENU },
                    modifier = Modifier.testTag("tech_back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = GravRingViolet)
                }
                Text("ДЕРЕВО ТЕХНОЛОГИЙ", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceCard,
                border = BorderStroke(1.dp, GravRingViolet)
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Icon(Icons.Default.Psychology, contentDescription = "Очки Технологий", tint = GravRingViolet, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${profile?.techPoints ?: 0} ОT", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(skills) { skill ->
                val isUnlocked = profile?.unlockedSkillsCsv?.contains(skill.id) == true

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    border = BorderStroke(1.dp, if (isUnlocked) GravRingViolet else DarkSurfaceCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(GravRingViolet.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(skill.icon, contentDescription = skill.title, tint = GravRingViolet)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(skill.title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                            Text(skill.description, fontSize = 11.sp, color = TextSecondary)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (isUnlocked) {
                            Text("ИЗУЧЕНО", fontWeight = FontWeight.Bold, color = GravRingViolet, fontSize = 11.sp)
                        } else {
                            Button(
                                onClick = {
                                    if ((profile?.techPoints ?: 0) >= skill.costTechPoints) {
                                        viewModel.unlockTechSkill(skill.id, skill.costTechPoints)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GravRingViolet)
                            ) {
                                Text("${skill.costTechPoints} OT", fontSize = 11.sp, color = DeepSpaceBackground, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
