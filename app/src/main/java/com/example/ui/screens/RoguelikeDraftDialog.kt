package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NomadViewModel
import com.example.ui.theme.*

@Composable
fun RoguelikeDraftDialog(
    viewModel: NomadViewModel
) {
    val choices by viewModel.roguelikeDraftChoices.collectAsState()
    val sector by viewModel.roguelikeSector.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBackground.copy(alpha = 0.95f))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ЧЕКПОИНТ СЕКТОРА $sector", fontSize = 22.sp, fontWeight = FontWeight.Black, color = NeonCyan)
            Text("Выберите 1 модуль для улучшения ровера:", fontSize = 13.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(choices) { module ->
                    Card(
                        onClick = { viewModel.applyRoguelikeUpgrade(module) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                        border = BorderStroke(2.dp, module.rarity.color),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(module.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = module.rarity.color.copy(alpha = 0.2f)
                                ) {
                                    Text(module.rarity.displayName, color = module.rarity.color, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(module.description, fontSize = 12.sp, color = TextSecondary)
                            if (module.specialPerk.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Эффект: ${module.specialPerk}", fontSize = 11.sp, color = NeonAmber, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
