package com.example.saltpepper.authentication.di

import com.example.saltpepper.authentication.viewmodel.LoginScreenViewModel
import com.example.saltpepper.authentication.viewmodel.RegisterScreenViewModel
import com.example.saltpepper.authentication.viewmodel.VerifyEmailCodeViewModel
import com.example.saltpepper.data.repository.AuthRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    viewModel { LoginScreenViewModel(get<AuthRepository>()) }
    viewModel { RegisterScreenViewModel(get<AuthRepository>()) }
    viewModel { VerifyEmailCodeViewModel(get<AuthRepository>()) }
}