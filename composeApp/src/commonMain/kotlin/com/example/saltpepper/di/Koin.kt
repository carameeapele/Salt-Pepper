package com.example.saltpepper.di

import com.example.saltpepper.HomeViewModel
import com.example.saltpepper.authentication.di.authModule
import com.example.saltpepper.core.network.di.networkModule
import com.example.saltpepper.core.session.di.sessionModule
import com.example.saltpepper.data.authentication.di.authenticationDataModule
import com.example.saltpepper.data.profile.di.profileDataModule
import com.example.saltpepper.data.recipe.di.recipeDataModule
import com.example.saltpepper.profile.viewmodel.FavoritesViewModel
import com.example.saltpepper.profile.viewmodel.HouseholdSetupAfterRegistrationViewModel
import com.example.saltpepper.profile.viewmodel.ProfileViewModel
import com.example.saltpepper.profile.viewmodel.SettingsViewModel
import com.example.saltpepper.splash.di.splashModule
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::FavoritesViewModel)
    viewModelOf(::HouseholdSetupAfterRegistrationViewModel)
}

val allModules = listOf(
    sessionModule,
    networkModule,
    authenticationDataModule,
    profileDataModule,
    recipeDataModule,
    authModule,
    splashModule,
    appModule
)

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(allModules)
}