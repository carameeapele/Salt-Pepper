package com.example.saltpepper.navigation

import com.example.saltpepper.home.navigation.HomeDestinations
import com.example.saltpepper.menu.navigation.MenuDestinations
import com.example.saltpepper.profile.navigation.ProfileDestinations
import com.example.saltpepper.recipes.navigation.RecipesDestinations
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertEquals

class MainDestinationTest {
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun mainAndSectionRoutesHaveRuntimeSerializers() {
        val routes = listOf(
            MainDestination,
            HomeDestinations.Home,
            RecipesDestinations.Recipes,
            MenuDestinations.Menu,
            ProfileDestinations.Profile
        )

        routes.forEach { route ->
            val serializer = serializer(route::class, emptyList(), false)
            assertEquals(0, serializer.descriptor.elementsCount)
        }
    }
}