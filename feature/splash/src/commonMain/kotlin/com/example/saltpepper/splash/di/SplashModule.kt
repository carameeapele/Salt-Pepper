package com.example.saltpepper.splash.di

import org.koin.core.module.dsl.viewModel
import com.example.saltpepper.splash.viewmodel.SplashScreenViewModel
import org.koin.dsl.module

val splashModule = module {
    viewModel { SplashScreenViewModel() }
}