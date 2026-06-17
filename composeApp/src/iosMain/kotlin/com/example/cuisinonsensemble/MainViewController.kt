package com.example.cuisinonsensemble

import androidx.compose.ui.window.ComposeUIViewController
import com.example.cuisinonsensemble.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }