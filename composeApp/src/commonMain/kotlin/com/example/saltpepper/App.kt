package com.example.saltpepper

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import com.example.saltpepper.navigation.RootNavigation
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination

@Composable
@Preview
fun App() {
    SaltPepperTheme {
        RootNavigation()
    }
}