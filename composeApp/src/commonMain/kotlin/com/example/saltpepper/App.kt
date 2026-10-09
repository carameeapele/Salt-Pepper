package com.example.saltpepper

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import com.example.saltpepper.navigation.RootNavigation

@Composable
@Preview
fun App() {
    SaltPepperTheme {
        RootNavigation()
    }
}