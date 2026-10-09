package com.example.saltpepper.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.home_tab
import saltpepper.core.ui.generated.resources.menu_tab
import saltpepper.core.ui.generated.resources.profile_tab
import saltpepper.core.ui.generated.resources.recipes_tab

enum class MainNavigationDestinations(
    val icon: ImageVector,
    val label: StringResource
) {
    HOME(
        icon = Icons.Outlined.Home,
        label = Res.string.home_tab
    ),
    MENU(
        icon = Icons.AutoMirrored.Outlined.MenuBook,
        label = Res.string.menu_tab
    ),
    RECIPES(
        icon = Icons.Filled.Restaurant,
        label = Res.string.recipes_tab
    ),
    PROFILE(
        icon = Icons.Outlined.Face,
        label = Res.string.profile_tab
    )
}