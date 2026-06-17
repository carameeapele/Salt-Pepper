package com.example.cuisinonsensemble

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.cuisinonsensemble.navigation.RootNavigation
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination

@Composable
@Preview
fun App() {
    MaterialTheme {
        RootNavigation()
    }
}