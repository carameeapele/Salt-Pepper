package com.example.saltpepper.recipes.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.saltpepper.recipes.screen.RecipesScreen

fun NavGraphBuilder.recipesNavigation() {
    composable<RecipesDestinations.Recipes> {
        RecipesScreen()
    }
}