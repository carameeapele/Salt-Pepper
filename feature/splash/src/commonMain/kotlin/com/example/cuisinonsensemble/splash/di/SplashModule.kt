package com.example.cuisinonsensemble.splash.di

import org.koin.core.module.dsl.viewModel
import com.example.cuisinonsensemble.splash.viewmodel.SplashScreenViewModel
import org.koin.dsl.module

val splashModule = module {
    viewModel { SplashScreenViewModel() }
}