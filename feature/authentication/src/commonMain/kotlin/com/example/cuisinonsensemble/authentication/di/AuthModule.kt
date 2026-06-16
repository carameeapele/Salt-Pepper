package com.example.cuisinonsensemble.authentication.di

import com.example.cuisinonsensemble.authentication.viewmodel.LoginScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {
    viewModelOf(::LoginScreenViewModel)
}