package com.example.saltpepper.profile.viewmodel

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HouseholdSetupAfterRegistrationViewModelTest {
    @Test
    fun householdCountsHaveSafeMinimums() {
        val viewModel = HouseholdSetupAfterRegistrationViewModel()
        repeat(3) {
            viewModel.changeAdults(false)
            viewModel.changeChildren(false)
        }
        assertEquals(1, viewModel.uiState.value.adults)
        assertEquals(0, viewModel.uiState.value.children)
        viewModel.changeAdults(true)
        viewModel.changeChildren(true)
        assertEquals(2, viewModel.uiState.value.adults)
        assertEquals(1, viewModel.uiState.value.children)
    }

    @Test
    fun navigationPreservesSelectionsAndReopeningStartsAtHousehold() {
        val viewModel = HouseholdSetupAfterRegistrationViewModel()
        viewModel.changeChildren(true)
        viewModel.next()
        viewModel.toggle(SetupOption.VEGAN)
        viewModel.toggle(SetupOption.MILK)
        viewModel.next()
        viewModel.toggle(SetupOption.JAPANESE)
        viewModel.next()
        assertEquals(2, viewModel.uiState.value.step)
        viewModel.back()
        assertEquals(1, viewModel.uiState.value.step)
        viewModel.start()
        assertEquals(0, viewModel.uiState.value.step)
        assertEquals(1, viewModel.uiState.value.children)
        assertEquals(
            setOf(SetupOption.VEGAN, SetupOption.MILK, SetupOption.JAPANESE),
            viewModel.uiState.value.selections
        )
        viewModel.toggle(SetupOption.MILK)
        assertTrue(SetupOption.MILK !in viewModel.uiState.value.selections)
        repeat(3) { viewModel.back() }
        assertEquals(0, viewModel.uiState.value.step)
    }

    @Test
    fun resetClearsSessionPreferences() {
        val viewModel = HouseholdSetupAfterRegistrationViewModel()
        viewModel.next()
        viewModel.changeAdults(true)
        viewModel.toggle(SetupOption.PEANUTS)
        viewModel.reset()
        assertEquals(HouseholdSetupAfterRegistrationUiState(), viewModel.uiState.value)
    }
}
