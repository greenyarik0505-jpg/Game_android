package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ActiveAbility
import com.example.engine.GravityMode
import com.example.ui.NomadViewModel
import com.example.ui.ScreenState
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun GamePlayScreen(
    viewModel: NomadViewModel
) {
    val physics = viewModel.activePhysics
    val terrain = viewModel.activeTerrain
    val stats by viewModel.liveRunStats.collectAsState()
    val biome by viewModel.selectedBiome.collectAsState()

    // Game loop tick ~60 FPS
    LaunchedEffect(Unit) {
        var lastTime = System.nanoTime()
        while (true) {
            val now = System.nanoTime()
            val dt = ((now - lastTime) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
            lastTime = now
            viewModel.updateGameLoop(dt)
            delay(16) // ~60fps
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(biome.skyColor)
    ) {
        // 1. Physics Canvas Rendering
        if (physics != null && terrain != null) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Camera follow offset
                val cameraOffsetX = canvasWidth * 0.35f - physics.pos.x
                val cameraOffsetY = canvasHeight * 0.55f - physics.pos.y

                // Draw Stars / Sky Grid
                for (i in 0..20) {
                    val starX = (i * 90f + cameraOffsetX * 0.1f) % canvasWidth
                    val starY = (i * 45f + cameraOffsetY * 0.1f) % canvasHeight
                    drawCircle(color = Color.White.copy(alpha = 0.3f), radius = 2f, center = Offset(starX, starY))
                }

                // Draw Terrain Ground (Visible points only for 60FPS performance)
                val minX = physics.pos.x - canvasWidth * 0.8f
                val maxX = physics.pos.x + canvasWidth * 1.2f
                val visiblePoints = terrain.getVisibleGroundPoints(minX, maxX)

                if (visiblePoints.isNotEmpty()) {
                    val groundPath = Path()
                    var first = true
                    visiblePoints.forEach { pt ->
                        val screenX = pt.x + cameraOffsetX
                        val screenY = pt.y + cameraOffsetY
                        if (first) {
                            groundPath.moveTo(screenX, screenY)
                            first = false
                        } else {
                            groundPath.lineTo(screenX, screenY)
                        }
                    }
                    groundPath.lineTo(canvasWidth + 200f, canvasHeight + 400f)
                    groundPath.lineTo(-200f, canvasHeight + 400f)
                    groundPath.close()

                    // Terrain Body Fill
                    drawPath(
                        path = groundPath,
                        color = biome.primaryColor.copy(alpha = 0.95f)
                    )
                    // Glowing Neon Top Terrain Line
                    drawPath(
                        path = groundPath,
                        color = biome.defaultSurface.color,
                        style = Stroke(width = 5f)
                    )
                }

                // Draw Visible Energy Crystals
                terrain.getCrystals().forEach { crystal ->
                    if (!crystal.collected && crystal.position.x in minX..maxX) {
                        val screenX = crystal.position.x + cameraOffsetX
                        val screenY = crystal.position.y + cameraOffsetY
                        val crystalColor = if (crystal.isSuperCrystal) NeonAmber else NeonCyan
                        drawCircle(color = crystalColor.copy(alpha = 0.3f), radius = if (crystal.isSuperCrystal) 14f else 9f, center = Offset(screenX, screenY))
                        drawCircle(color = crystalColor, radius = if (crystal.isSuperCrystal) 8f else 5f, center = Offset(screenX, screenY))
                    }
                }

                // Draw Particles
                viewModel.particleSystem.getActiveParticles().forEach { p ->
                    val px = p.position.x + cameraOffsetX
                    val py = p.position.y + cameraOffsetY
                    val alpha = (p.currentLife / p.maxLife).coerceIn(0f, 1f)
                    drawCircle(color = p.color.copy(alpha = alpha), radius = p.size, center = Offset(px, py))
                }

                // Draw Vehicle
                val roverScreenX = physics.pos.x + cameraOffsetX
                val roverScreenY = physics.pos.y + cameraOffsetY

                rotate(degrees = Math.toDegrees(physics.angleRad.toDouble()).toFloat(), pivot = Offset(roverScreenX, roverScreenY)) {
                    // Shield Forcefield Aura
                    if (physics.isShieldActive) {
                        drawCircle(
                            color = NeonCyan.copy(alpha = 0.45f),
                            radius = 42f,
                            center = Offset(roverScreenX, roverScreenY),
                            style = Stroke(width = 5f)
                        )
                        drawCircle(
                            color = NeonCyan.copy(alpha = 0.15f),
                            radius = 42f,
                            center = Offset(roverScreenX, roverScreenY)
                        )
                    }

                    // Chassis Hull
                    val chassisPath = Path().apply {
                        moveTo(roverScreenX - 30f, roverScreenY - 14f)
                        lineTo(roverScreenX + 30f, roverScreenY - 14f)
                        lineTo(roverScreenX + 40f, roverScreenY + 8f)
                        lineTo(roverScreenX - 34f, roverScreenY + 8f)
                        close()
                    }
                    drawPath(path = chassisPath, color = NeonCyan)
                    drawPath(path = chassisPath, color = Color.White.copy(alpha = 0.4f), style = Stroke(width = 2f))

                    // Cockpit Canopy Window
                    val cockpitPath = Path().apply {
                        moveTo(roverScreenX - 10f, roverScreenY - 12f)
                        lineTo(roverScreenX + 20f, roverScreenY - 12f)
                        lineTo(roverScreenX + 26f, roverScreenY + 0f)
                        lineTo(roverScreenX - 10f, roverScreenY + 0f)
                        close()
                    }
                    drawPath(path = cockpitPath, color = DeepSpaceBackground)
                    drawPath(path = cockpitPath, color = NeonPink, style = Stroke(width = 1.5f))

                    // Wheels with Rotating Spokes
                    if (!physics.isWheelsDetached) {
                        val rearWheelX = roverScreenX - 22f
                        val frontWheelX = roverScreenX + 22f
                        val wheelY = roverScreenY + 10f
                        val wheelRotation = (physics.pos.x / 8f) % 360f

                        // Rear Wheel
                        drawCircle(color = NeonPink, radius = 11f, center = Offset(rearWheelX, wheelY))
                        drawCircle(color = DarkSurfaceCard, radius = 6f, center = Offset(rearWheelX, wheelY))
                        rotate(degrees = wheelRotation, pivot = Offset(rearWheelX, wheelY)) {
                            drawLine(color = Color.White, start = Offset(rearWheelX - 5f, wheelY), end = Offset(rearWheelX + 5f, wheelY), strokeWidth = 2f)
                            drawLine(color = Color.White, start = Offset(rearWheelX, wheelY - 5f), end = Offset(rearWheelX, wheelY + 5f), strokeWidth = 2f)
                        }

                        // Front Wheel
                        drawCircle(color = NeonPink, radius = 11f, center = Offset(frontWheelX, wheelY))
                        drawCircle(color = DarkSurfaceCard, radius = 6f, center = Offset(frontWheelX, wheelY))
                        rotate(degrees = wheelRotation, pivot = Offset(frontWheelX, wheelY)) {
                            drawLine(color = Color.White, start = Offset(frontWheelX - 5f, wheelY), end = Offset(frontWheelX + 5f, wheelY), strokeWidth = 2f)
                            drawLine(color = Color.White, start = Offset(frontWheelX, wheelY - 5f), end = Offset(frontWheelX, wheelY + 5f), strokeWidth = 2f)
                        }
                    }
                }

                // Draw Detached Debris
                physics.detachedDebrisList.forEach { debris ->
                    val dx = debris.position.x + cameraOffsetX
                    val dy = debris.position.y + cameraOffsetY
                    drawRect(color = OverheatCrimson, topLeft = Offset(dx - 8f, dy - 8f), size = Size(16f, 16f))
                }
            }
        }

        // 2. HUD Overlay
        if (physics != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                // Top Row: Speed, Distance, Crystals & Gravity Vector Compass
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${stats.distanceMeters.toInt()}м",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonCyan
                        )
                        val speedKmh = (maxOf(0f, physics.vel.x) * 0.45f).toInt()
                        Text(
                            text = "СКОРОСТЬ: $speedKmh КМ/Ч",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }

                    // Gravity Compass
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurfaceCard,
                        border = BorderStroke(1.5.dp, NeonPink)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = when (physics.currentGravityMode) {
                                    GravityMode.DOWN -> Icons.Default.ArrowDownward
                                    GravityMode.UP -> Icons.Default.ArrowUpward
                                    GravityMode.LEFT -> Icons.AutoMirrored.Filled.ArrowBack
                                    GravityMode.RIGHT -> Icons.AutoMirrored.Filled.ArrowForward
                                    GravityMode.ZERO_G -> Icons.Default.Waves
                                    GravityMode.PULSE -> Icons.Default.South
                                },
                                contentDescription = "Вектор гравитации",
                                tint = NeonPink
                            )
                            Text(
                                text = physics.currentGravityMode.displayName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    // Pause Button
                    IconButton(
                        onClick = { viewModel.currentScreen.value = ScreenState.MENU },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(DarkSurfaceCard)
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = "Пауза", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Energy & Heat Status Bars (Max Width 300dp to avoid screen stretching)
                Column(
                    modifier = Modifier.widthIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Energy Bar
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("ЭНЕРГИЯ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonEmerald)
                        LinearProgressIndicator(
                            progress = { (physics.currentEnergy / physics.maxEnergy).coerceIn(0f, 1f) },
                            color = NeonEmerald,
                            trackColor = DarkSurfaceCard,
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }

                    // Heat Bar
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("ПЕРЕГРЕВ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OverheatCrimson)
                        LinearProgressIndicator(
                            progress = { (physics.currentHeat / physics.maxHeat).coerceIn(0f, 1f) },
                            color = OverheatCrimson,
                            trackColor = DarkSurfaceCard,
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                }
            }

            // 3. Touch Controls Overlay at Bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                // Left Controls: Tilt Balance & Gravity Shift
                Row(
                    modifier = Modifier.align(Alignment.BottomStart),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Lean Left
                    ControlButton(
                        icon = Icons.Default.RotateLeft,
                        label = "НАКЛОН Л",
                        color = NeonCyan,
                        onPress = { physics.leanInput = -1.0f },
                        onRelease = { physics.leanInput = 0f },
                        testTag = "btn_lean_left"
                    )

                    // Lean Right
                    ControlButton(
                        icon = Icons.Default.RotateRight,
                        label = "НАКЛОН П",
                        color = NeonCyan,
                        onPress = { physics.leanInput = 1.0f },
                        onRelease = { physics.leanInput = 0f },
                        testTag = "btn_lean_right"
                    )

                    // Dynamic Gravity Shift Button
                    IconButton(
                        onClick = { viewModel.performGravityShift() },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceCard)
                            .border(2.dp, NeonPink, CircleShape)
                            .testTag("btn_gravity_shift")
                    ) {
                        Icon(Icons.Default.Flip, contentDescription = "Инверсия гравитации", tint = NeonPink, modifier = Modifier.size(28.dp))
                    }
                }

                // Right Controls: Ability & Gas / Brake Pedals
                Row(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Ability Trigger Button
                    IconButton(
                        onClick = {
                            physics.activateAbility(
                                ActiveAbility.SHIELD,
                                viewModel.particleSystem,
                                viewModel.audioSynthesizer
                            )
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceCard)
                            .border(2.dp, NeonAmber, CircleShape)
                            .testTag("btn_ability_trigger")
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = "Способность", tint = NeonAmber, modifier = Modifier.size(28.dp))
                    }

                    // Brake / Reverse
                    ControlButton(
                        icon = Icons.Default.FastRewind,
                        label = "ТОРМОЗ",
                        color = OverheatCrimson,
                        onPress = { physics.throttleInput = -0.8f },
                        onRelease = { physics.throttleInput = 0f },
                        testTag = "btn_brake"
                    )

                    // Gas Throttle
                    ControlButton(
                        icon = Icons.Default.FastForward,
                        label = "ГАЗ",
                        color = NeonEmerald,
                        onPress = { physics.throttleInput = 1.0f },
                        onRelease = { physics.throttleInput = 0f },
                        testTag = "btn_gas"
                    )
                }
            }
        }
    }
}

@Composable
fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurfaceCard)
            .border(2.dp, color, RoundedCornerShape(18.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onPress()
                        tryAwaitRelease()
                        onRelease()
                    }
                )
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(26.dp))
            Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
