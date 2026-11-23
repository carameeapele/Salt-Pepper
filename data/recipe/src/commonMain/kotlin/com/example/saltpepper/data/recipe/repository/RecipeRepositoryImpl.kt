package com.example.saltpepper.data.recipe.repository

import com.example.saltpepper.core.network.model.RecipeApiModel
import com.example.saltpepper.data.recipe.datasource.RecipeRemoteDataSource
import com.example.saltpepper.data.recipe.model.Recipe
import kotlinx.coroutines.CancellationException

class RecipeRepositoryImpl(private val remoteDataSource: RecipeRemoteDataSource) : RecipeRepository {
    override suspend fun getMyRecipes(): Result<List<Recipe>> =
        request { remoteDataSource.getMyRecipes() }

    override suspend fun getFavorites(): Result<List<Recipe>> =
        request { remoteDataSource.getFavorites() }

    private suspend fun request(block: suspend () -> List<RecipeApiModel>): Result<List<Recipe>> = try {
        Result.success(block().map { Recipe(it.id, it.title, it.totalTimeMinutes) })
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        Result.failure(error)
    }
}
