package com.example.saltpepper.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class MainTransitionTest {
    @Test
    fun slideDirectionFollowsBottomBarOrderForEveryPair() {
        val sections = listOf(
            MainNavigationDestinations.HOME,
            MainNavigationDestinations.MENU,
            MainNavigationDestinations.RECIPES,
            MainNavigationDestinations.PROFILE
        )
        sections.forEachIndexed { sourceIndex, source ->
            sections.forEachIndexed { targetIndex, target ->
                assertEquals(
                    targetIndex.compareTo(sourceIndex),
                    sectionTransitionDirection(source, target),
                    "$source -> $target"
                )
            }
        }
    }

    @Test
    fun unknownSectionsDoNotSlide() {
        assertEquals(0, sectionTransitionDirection(null, MainNavigationDestinations.HOME))
        assertEquals(0, sectionTransitionDirection(MainNavigationDestinations.HOME, null))
        assertEquals(0, sectionTransitionDirection(null, null))
    }
}