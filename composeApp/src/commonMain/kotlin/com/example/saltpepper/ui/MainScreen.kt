package com.example.saltpepper.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import com.example.saltpepper.navigation.MainNavigation
import com.example.saltpepper.navigation.MainNavigationDestinations
import com.example.saltpepper.navigation.mainSection
import com.example.saltpepper.navigation.navigateToSection
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            MainNavigationBottomBar(
                selectedSection = backStackEntry?.destination?.mainSection(),
                onSectionSelected = { navController.navigateToSection(it) }
            )
        }
    ) { contentPadding ->
        MainNavigation(
            navController = navController,
            modifier = Modifier.fillMaxSize()
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding)
        )
    }
}

@Composable
fun MainNavigationBottomBar(
    selectedSection: MainNavigationDestinations?,
    onSectionSelected: (MainNavigationDestinations) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        MainNavigationDestinations.entries.forEach { destination ->
            val label = destination.label
            NavigationBarItem(
                selected = selectedSection == destination,
                onClick = { onSectionSelected(destination) },
                icon = { Icon(
                    imageVector = destination.icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                ) },
                label = {
                    Text(
                        text = stringResource(label),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                    unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Preview
@Composable
private fun MainNavigationBottomBarPreview() {
    SaltPepperTheme {
        MainNavigationBottomBar(
            selectedSection = MainNavigationDestinations.HOME,
            onSectionSelected = {}
        )
    }
}