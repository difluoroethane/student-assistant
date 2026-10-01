package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

data class BrutalPalette(
    val border: Color,
    val shadow: Color,
    val surface: Color,
    val background: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accentYellow: Color,
    val accentOrange: Color,
    val accentGreen: Color,
    val isDark: Boolean
)

val LocalBrutalPalette = staticCompositionLocalOf {
    BrutalPalette(
        border = BrutalBlack,
        shadow = BrutalBlack,
        surface = BrutalWhite,
        background = BrutalCream,
        textPrimary = BrutalBlack,
        textSecondary = Color(0xFF555555),
        accentYellow = BrutalYellow,
        accentOrange = BrutalOrange,
        accentGreen = BrutalGreen,
        isDark = false
    )
}

private val LightBrutalPalette = BrutalPalette(
    border = BrutalBlack,
    shadow = BrutalBlack,
    surface = BrutalWhite,
    background = BrutalCream,
    textPrimary = BrutalBlack,
    textSecondary = Color(0xFF555555),
    accentYellow = BrutalYellow,
    accentOrange = BrutalOrange,
    accentGreen = BrutalGreen,
    isDark = false
)

private val DarkBrutalPalette = BrutalPalette(
    border = BrutalWhite,
    shadow = BrutalNeonYellow,
    surface = BrutalDarkCharcoal,
    background = BrutalPitchBlack,
    textPrimary = BrutalWhite,
    textSecondary = Color(0xFFCCCCCC),
    accentYellow = BrutalNeonYellow,
    accentOrange = BrutalOrange,
    accentGreen = BrutalNeonGreen,
    isDark = true
)

private val LightColorScheme = lightColorScheme(
    primary = BrutalOrange,
    onPrimary = BrutalWhite,
    secondary = BrutalYellow,
    onSecondary = BrutalBlack,
    tertiary = BrutalGreen,
    onTertiary = BrutalWhite,
    background = BrutalCream,
    onBackground = BrutalBlack,
    surface = BrutalWhite,
    onSurface = BrutalBlack,
    error = CatUrgent,
    onError = BrutalWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = BrutalOrange,
    onPrimary = BrutalWhite,
    secondary = BrutalNeonYellow,
    onSecondary = BrutalBlack,
    tertiary = BrutalNeonGreen,
    onTertiary = BrutalBlack,
    background = BrutalPitchBlack,
    onBackground = BrutalWhite,
    surface = BrutalDarkCharcoal,
    onSurface = BrutalWhite,
    error = CatUrgent,
    onError = BrutalWhite
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val brutalPalette = if (darkTheme) DarkBrutalPalette else LightBrutalPalette

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalBrutalPalette provides brutalPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = BrutalTypography,
            content = content
        )
    }
}
