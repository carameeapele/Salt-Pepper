package com.example.cuisinonsensemble.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary500,
    onPrimary = surfaceLight,
    primaryContainer = Primary100,
    onPrimaryContainer = Primary900,

    secondary = Secondary500,
    onSecondary = surfaceLight,
    secondaryContainer = Secondary100,
    onSecondaryContainer = Secondary900,

    background = surfaceLight,
    onBackground = Grey900,
    surface = surfaceLight,
    onSurface = Grey900,
    surfaceVariant = Grey50,
    onSurfaceVariant = Grey700,

    error = Red500,
    onError = surfaceLight,
    errorContainer = Red50,
    onErrorContainer = Red900,

    outline = Grey300,
    outlineVariant = Grey100,
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
    onBackground = Grey50,
    surface = Primary800,
    onSurface = Grey50,
    surfaceVariant = Grey900,
    onSurfaceVariant = Grey300,

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
        typography = RobotoMonoTypography(),
        content = content
    )
}