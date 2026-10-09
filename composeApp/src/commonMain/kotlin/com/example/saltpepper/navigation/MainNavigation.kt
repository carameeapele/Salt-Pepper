package com.example.saltpepper.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.saltpepper.home.navigation.HomeDestinations
import com.example.saltpepper.home.navigation.homeNavigation
import com.example.saltpepper.menu.navigation.MenuDestinations
import com.example.saltpepper.menu.navigation.menuNavigation
import com.example.saltpepper.profile.navigation.ProfileDestinations
import com.example.saltpepper.profile.navigation.profileNavigation
import com.example.saltpepper.recipes.navigation.RecipesDestinations
import com.example.saltpepper.recipes.navigation.recipesNavigation
import kotlinx.serialization.Serializable

@Serializable
object MainDestination

@Composable
fun MainNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestinations.Home,
        modifier = modifier,
        enterTransition = {
            val direction = sectionTransitionDirection(
                initialState.destination.mainSection(), targetState.destination.mainSection()
            )
            slideInHorizontally(initialOffsetX = { width -> width * direction })
        },
        exitTransition = {
            val direction = sectionTransitionDirection(
                initialState.destination.mainSection(), targetState.destination.mainSection()
            )
            slideOutHorizontally(targetOffsetX = { width -> -width * direction })
        },
        popEnterTransition = {
            val direction = sectionTransitionDirection(
                initialState.destination.mainSection(), targetState.destination.mainSection()
            )
            slideInHorizontally(initialOffsetX = { width -> width * direction })
        },
        popExitTransition = {
            val direction = sectionTransitionDirection(
                initialState.destination.mainSection(), targetState.destination.mainSection()
            )
            slideOutHorizontally(targetOffsetX = { width -> -width * direction })
        }
    ) {
        homeNavigation(
            onBrowseRecipes = { navController.navigateToSection(MainNavigationDestinations.RECIPES) },
            onPlanMeals = { navController.navigateToSection(MainNavigationDestinations.MENU) }
        )
        recipesNavigation()
        menuNavigation()
        profileNavigation()
    }
}

internal fun NavDestination.mainSection(): MainNavigationDestinations? =
    hierarchy.firstNotNullOfOrNull { destination ->
        when {
            destination.hasRoute<HomeDestinations.Home>() -> MainNavigationDestinations.HOME
            destination.hasRoute<RecipesDestinations.Recipes>() -> MainNavigationDestinations.RECIPES
            destination.hasRoute<MenuDestinations.Menu>() -> MainNavigationDestinations.MENU
            destination.hasRoute<ProfileDestinations.Profile>() -> MainNavigationDestinations.PROFILE
            else -> null
        }
    }

internal fun sectionTransitionDirection(
    from: MainNavigationDestinations?,
    to: MainNavigationDestinations?
): Int {
    if (from == null || to == null) return 0
    return to.ordinal.compareTo(from.ordinal)
}

internal fun NavHostController.navigateToSection(section: MainNavigationDestinations) {
    if (currentDestination?.mainSection() == section) return

    val route = when (section) {
        MainNavigationDestinations.HOME -> HomeDestinations.Home
        MainNavigationDestinations.RECIPES -> RecipesDestinations.Recipes
        MainNavigationDestinations.MENU -> MenuDestinations.Menu
        MainNavigationDestinations.PROFILE -> ProfileDestinations.Profile
    }
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}