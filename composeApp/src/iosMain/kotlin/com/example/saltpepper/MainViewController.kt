package com.example.saltpepper

import androidx.compose.ui.window.ComposeUIViewController
import com.example.saltpepper.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }