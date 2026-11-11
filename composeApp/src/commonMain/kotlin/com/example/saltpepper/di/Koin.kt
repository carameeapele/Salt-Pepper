package com.example.saltpepper.di

import com.example.saltpepper.HomeViewModel
import com.example.saltpepper.authentication.di.authModule
import com.example.saltpepper.data.di.dataModule
import com.example.saltpepper.profile.viewmodel.FavoritesViewModel
import com.example.saltpepper.profile.viewmodel.ProfileSetupViewModel
import com.example.saltpepper.profile.viewmodel.ProfileViewModel
import com.example.saltpepper.splash.di.splashModule
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::FavoritesViewModel)
    viewModelOf(::ProfileSetupViewModel)
}

val allModules = listOf(
    dataModule,
    authModule,
    splashModule,
    appModule
)

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(allModules)
}