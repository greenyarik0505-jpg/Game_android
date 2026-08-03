package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GravityNomadColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = DeepSpaceBackground,
    primaryContainer = DarkSurfaceCard,
    onPrimaryContainer = NeonCyan,
    secondary = NeonPink,
    onSecondary = DeepSpaceBackground,
    tertiary = NeonAmber,
    background = DeepSpaceBackground,
    onBackground = TextPrimary,
    surface = DarkSurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceCardBorder,
    onSurfaceVariant = TextSecondary,
    error = OverheatCrimson
)

@Composable
fun GravityNomadTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GravityNomadColorScheme,
        typography = Typography,
        content = content
    )
}
