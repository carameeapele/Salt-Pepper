package com.example.saltpepper.menu.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.saltpepper.menu.screen.MenuScreen

fun NavGraphBuilder.menuNavigation() {
    composable<MenuDestinations.Menu> {
        MenuScreen()
    }
}