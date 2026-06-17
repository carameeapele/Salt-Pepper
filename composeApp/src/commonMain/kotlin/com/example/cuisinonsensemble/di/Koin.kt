package com.example.cuisinonsensemble.di

import com.example.cuisinonsensemble.HomeViewModel
import com.example.cuisinonsensemble.authentication.di.authModule
import com.example.cuisinonsensemble.data.di.dataModule
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::HomeViewModel)
}

val allModules = listOf(
    dataModule,
    authModule,
    appModule
)

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(allModules)
}