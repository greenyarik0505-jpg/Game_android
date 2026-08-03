package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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

@Composable
fun QuestsScreen(
    viewModel: NomadViewModel
) {
    val quests by viewModel.dailyQuests.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBackground)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { viewModel.currentScreen.value = ScreenState.MENU },
                modifier = Modifier.testTag("quests_back_button")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = NeonAmber)
            }
            Text("ЕЖЕДНЕВНЫЕ ЗАДАНИЯ НОМАДА", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(quests) { quest ->
                val progress = (quest.currentAmount.toFloat() / quest.targetAmount).coerceAtMost(1.0f)
                val isCompleted = quest.currentAmount >= quest.targetAmount

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    border = BorderStroke(1.dp, if (quest.isClaimed) DarkSurfaceCardBorder else NeonAmber),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(quest.title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                            Text("${quest.rewardCrystals} 💎", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(quest.description, fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            color = NeonAmber,
                            trackColor = DarkSurfaceCardBorder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (quest.isClaimed) {
                            Text("ПОЛУЧЕНО", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        } else if (isCompleted) {
                            Button(
                                onClick = { viewModel.claimQuest(quest) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonAmber)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Получить", tint = DeepSpaceBackground)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ЗАБРАТЬ НАГРАДУ", color = DeepSpaceBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        } else {
                            Text("${quest.currentAmount} / ${quest.targetAmount}", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}
