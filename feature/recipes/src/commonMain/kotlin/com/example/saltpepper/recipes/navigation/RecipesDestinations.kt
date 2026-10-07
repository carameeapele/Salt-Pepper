package com.example.saltpepper.recipes.navigation

import kotlinx.serialization.Serializable

sealed class RecipesDestinations {
    @Serializable
    data object Recipes : RecipesDestinations()
}