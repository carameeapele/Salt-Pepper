package com.example.saltpepper.data.recipe.repository

import com.example.saltpepper.data.recipe.model.Recipe

interface RecipeRepository {
    suspend fun getMyRecipes(): Result<List<Recipe>>
    suspend fun getFavorites(): Result<List<Recipe>>
}
