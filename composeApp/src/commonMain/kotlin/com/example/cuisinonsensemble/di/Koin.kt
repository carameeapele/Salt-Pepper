package com.example.cuisinonsensemble.di

import com.example.cuisinonsensemble.HomeViewModel
import com.example.cuisinonsensemble.data.AuthRepository
import com.example.cuisinonsensemble.data.SupabaseClientProvider
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    single { SupabaseClientProvider.client }
    single { AuthRepository(get()) }
}
val viewModelModule = module {
    viewModelOf(::HomeViewModel)
}

val sharedModules = listOf(appModule, viewModelModule)

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(sharedModules)
}