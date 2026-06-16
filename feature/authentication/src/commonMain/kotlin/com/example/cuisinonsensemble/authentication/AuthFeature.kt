package com.example.cuisinonsensemble.authentication

import org.koin.dsl.module

val authModule = module {
    single { AuthRepository(get()) }
    factory { AuthViewModel(get()) }
}