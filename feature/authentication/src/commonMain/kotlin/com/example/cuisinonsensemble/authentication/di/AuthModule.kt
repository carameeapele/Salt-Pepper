package com.example.cuisinonsensemble.authentication.di

import com.example.cuisinonsensemble.authentication.viewmodel.LoginScreenViewModel
import com.example.cuisinonsensemble.authentication.viewmodel.RegisterScreenViewModel
import com.example.cuisinonsensemble.data.repository.AuthRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    viewModel { LoginScreenViewModel(get<AuthRepository>()) }
    viewModel { RegisterScreenViewModel(get<AuthRepository>()) }
}