package com.example.saltpepper.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary500,
    onPrimary = SurfaceLight,
    primaryContainer = Primary100,
    onPrimaryContainer = Primary900,

    secondary = Secondary500,
    onSecondary = SurfaceLight,
    secondaryContainer = Secondary100,
    onSecondaryContainer = Secondary900,

    background = White,
    onBackground = Grey900,
    surface = SurfaceLight,
    onSurface = Grey900,
    surfaceVariant = Grey50,
    onSurfaceVariant = Grey700,
    surfaceTint = Grey100,

    error = Red500,
    onError = SurfaceLight,
    errorContainer = Red50,
    onErrorContainer = Red900,

    outline = Grey500,
    outlineVariant = Grey200,
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary400,
    onPrimary = Primary900,
    primaryContainer = Primary700,
    onPrimaryContainer = Primary50,

    secondary = Secondary400,
    onSecondary = Secondary900,
    secondaryContainer = Secondary700,
    onSecondaryContainer = Secondary50,

    background = Primary900,
    onBackground = Grey500,
    surface = Primary800,
    onSurface = Grey50,
    surfaceVariant = Grey900,
    onSurfaceVariant = Grey300,
    surfaceTint = Grey100,

    error = Red300,
    onError = Red900,
    errorContainer = Red700,
    onErrorContainer = Red50,

    outline = Grey600,
    outlineVariant = Grey800,
)

@Composable
fun SaltPepperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SaltPepperTypography(),
        content = content
    )
}