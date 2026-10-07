package com.example.saltpepper.home.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.browse_recipes_action
import saltpepper.core.ui.generated.resources.home_tab
import saltpepper.core.ui.generated.resources.home_welcome_subtitle
import saltpepper.core.ui.generated.resources.home_welcome_title
import saltpepper.core.ui.generated.resources.menu_empty_message
import saltpepper.core.ui.generated.resources.menu_screen_title
import saltpepper.core.ui.generated.resources.menu_tab
import saltpepper.core.ui.generated.resources.plan_meals_action
import saltpepper.core.ui.generated.resources.profile_empty_message
import saltpepper.core.ui.generated.resources.profile_screen_title
import saltpepper.core.ui.generated.resources.profile_tab
import saltpepper.core.ui.generated.resources.recipes_empty_message
import saltpepper.core.ui.generated.resources.recipes_screen_title
import saltpepper.core.ui.generated.resources.recipes_tab

private enum class HomeTab {
    HOME,
    RECIPES,
    MENU,
    PROFILE
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var selectedTab by rememberSaveable { mutableStateOf(HomeTab.HOME) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                HomeTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon(), contentDescription = null) },
                        label = { Text(stringResource(tab.labelResource())) }
                    )
                }
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            when (selectedTab) {
                HomeTab.HOME -> HomeContent(
                    onBrowseRecipes = { selectedTab = HomeTab.RECIPES },
                    onPlanMeals = { selectedTab = HomeTab.MENU }
                )
                HomeTab.RECIPES -> SectionContent(
                    title = stringResource(Res.string.recipes_screen_title),
                    message = stringResource(Res.string.recipes_empty_message)
                )
                HomeTab.MENU -> SectionContent(
                    title = stringResource(Res.string.menu_screen_title),
                    message = stringResource(Res.string.menu_empty_message)
                )
                HomeTab.PROFILE -> SectionContent(
                    title = stringResource(Res.string.profile_screen_title),
                    message = stringResource(Res.string.profile_empty_message)
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    onBrowseRecipes: () -> Unit,
    onPlanMeals: () -> Unit
) {
    Text(
        text = stringResource(Res.string.home_welcome_title),
        style = MaterialTheme.typography.headlineLarge
    )
    Spacer(Modifier.height(12.dp))
    Text(
        text = stringResource(Res.string.home_welcome_subtitle),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(36.dp))
    Button(onClick = onBrowseRecipes, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(Res.string.browse_recipes_action))
    }
    Spacer(Modifier.height(12.dp))
    Button(onClick = onPlanMeals, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(Res.string.plan_meals_action))
    }
}

@Composable
private fun SectionContent(title: String, message: String) {
    Text(text = title, style = MaterialTheme.typography.headlineLarge)
    Spacer(Modifier.height(16.dp))
    Text(
        text = message,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

private fun HomeTab.labelResource() = when (this) {
    HomeTab.HOME -> Res.string.home_tab
    HomeTab.RECIPES -> Res.string.recipes_tab
    HomeTab.MENU -> Res.string.menu_tab
    HomeTab.PROFILE -> Res.string.profile_tab
}

private fun HomeTab.icon() = when (this) {
    HomeTab.HOME -> Icons.Filled.Home
    HomeTab.RECIPES -> Icons.AutoMirrored.Filled.MenuBook
    HomeTab.MENU -> Icons.Filled.RestaurantMenu
    HomeTab.PROFILE -> Icons.Filled.PersonOutline
}