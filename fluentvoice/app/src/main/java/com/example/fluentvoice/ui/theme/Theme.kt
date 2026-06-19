package com.example.fluentvoice.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FluentVoiceColorScheme = lightColorScheme(
    primary = Navy,
    onPrimary = Surface,
    primaryContainer = NavyLight,
    onPrimaryContainer = Surface,
    secondary = Gold,
    onSecondary = TextNavy,
    secondaryContainer = GoldDim,
    onSecondaryContainer = Gold,
    background = BgColor,
    onBackground = TextNavy,
    surface = Surface,
    onSurface = TextNavy,
    surfaceVariant = Surface2,
    onSurfaceVariant = TextNavy,
    outline = Border,
    error = Red,
    onError = Surface
)

@Composable
fun FluentvoiceTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Navy.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = FluentVoiceColorScheme,
        typography = Typography,
        content = content
    )
}