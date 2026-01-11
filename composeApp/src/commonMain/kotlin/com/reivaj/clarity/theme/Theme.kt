package com.reivaj.clarity.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Define a Light Color Scheme based on the palette
private val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimary,
    primaryContainer = SecondaryBlue,
    onPrimaryContainer = ClarityWhite,
    secondary = SecondaryBlue,
    onSecondary = OnPrimary,
    tertiary = TertiaryBlue,
    onTertiary = OnBackground, // Check contrast
    background = LightBackground,
    onBackground = OnBackground,
    surface = LightSurface,
    onSurface = OnSurface,
    surfaceVariant = TertiaryBlue.copy(alpha = 0.3f),
    onSurfaceVariant = OnSurface
)

// Define a Dark Color Scheme (Optional, derived or simple dark mode for now)
private val DarkColors = darkColorScheme(
    primary = ClarityMediumBlue,
    onPrimary = OnBackground,
    background = OnBackground, // Dark grey/black
    surface = OnBackground,
    onSurface = ClarityWhite // White text
    // ... extend as needed
)

// Typography setup - Placeholder for Google Fonts (Outfit)
// In a real KMP app, you would load the font resource here.
val ClarityTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun ClarityTheme(
    useDarkTheme: Boolean = false, // Default to light mode
    content: @Composable () -> Unit
) {
    // For now, let's enforce Light Theme as requested by the palette visual
    // Or respect system setting if you prefer.
    // The palette provided was specifically light.
    val colors = if (useDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = ClarityTypography,
        content = content
    )
}
