package com.example.saltpepper.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.saltpepper.home.screen.HomeScreen

fun NavGraphBuilder.homeNavigation() {
    composable<HomeDestinations.Home> {
        HomeScreen()
    }
}