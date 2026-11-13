package com.example.saltpepper.data.recipe.di

import com.example.saltpepper.data.recipe.datasource.RecipeRemoteDataSource
import com.example.saltpepper.data.recipe.repository.RecipeRepository
import com.example.saltpepper.data.recipe.repository.RecipeRepositoryImpl
import org.koin.dsl.module

val recipeDataModule = module {
    single { RecipeRemoteDataSource(get()) }
    single<RecipeRepository> { RecipeRepositoryImpl(get()) }
}
