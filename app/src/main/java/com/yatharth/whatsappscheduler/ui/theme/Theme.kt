package com.yatharth.whatsappscheduler.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val EmeraldPrimary = Color(0xFF10B981)
val EmeraldOnPrimary = Color(0xFF000000)
val EmeraldContainer = Color(0xFF064E3B)
val EmeraldOnContainer = Color(0xFFA7F3D0)

val DarkBackground = Color(0xFF090D16)
val DarkSurface = Color(0xFF111827)
val DarkSurfaceVariant = Color(0xFF1F2937)
val DarkOnBackground = Color(0xFFF9FAFB)
val DarkOnSurface = Color(0xFFF3F4F6)
val DarkOnSurfaceVariant = Color(0xFF9CA3AF)
val DarkOutline = Color(0xFF374151)

val StatusScheduled = Color(0xFF3B82F6) // Blue
val StatusCompleted = Color(0xFF10B981) // Green
val StatusRequiresAction = Color(0xFFF59E0B) // Amber
val StatusFailed = Color(0xFFEF4444) // Red
val StatusMissed = Color(0xFF8B5CF6) // Purple
val StatusCancelled = Color(0xFF6B7280) // Gray

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldOnPrimary,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = EmeraldOnContainer,
    secondary = Color(0xFF34D399),
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

@Composable
fun WhatsAppSchedulerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
