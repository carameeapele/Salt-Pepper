package com.example.saltpepper.data.recipe.datasource

import com.example.saltpepper.core.network.SaltPepperApi

class RecipeRemoteDataSource(private val api: SaltPepperApi) {
    suspend fun getMyRecipes() = api.getMyRecipes()
    suspend fun getFavorites() = api.getFavoriteRecipes()
}
