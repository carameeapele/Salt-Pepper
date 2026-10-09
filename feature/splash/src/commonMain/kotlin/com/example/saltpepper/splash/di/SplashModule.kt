package com.example.saltpepper.splash.di

import com.example.saltpepper.splash.viewmodel.SplashScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val splashModule = module {
    viewModel { SplashScreenViewModel(get()) }
}