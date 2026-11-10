package com.example.saltpepper.profile.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.StringResource
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.setup_anti_inflammatory
import saltpepper.core.ui.generated.resources.setup_celery
import saltpepper.core.ui.generated.resources.setup_chinese
import saltpepper.core.ui.generated.resources.setup_cholesterol
import saltpepper.core.ui.generated.resources.setup_egg
import saltpepper.core.ui.generated.resources.setup_fish
import saltpepper.core.ui.generated.resources.setup_french
import saltpepper.core.ui.generated.resources.setup_gluten
import saltpepper.core.ui.generated.resources.setup_halal
import saltpepper.core.ui.generated.resources.setup_high_protein
import saltpepper.core.ui.generated.resources.setup_indian
import saltpepper.core.ui.generated.resources.setup_iron
import saltpepper.core.ui.generated.resources.setup_italian
import saltpepper.core.ui.generated.resources.setup_japanese
import saltpepper.core.ui.generated.resources.setup_low_sugar
import saltpepper.core.ui.generated.resources.setup_lupin
import saltpepper.core.ui.generated.resources.setup_mediterranean
import saltpepper.core.ui.generated.resources.setup_mexican
import saltpepper.core.ui.generated.resources.setup_middle_eastern
import saltpepper.core.ui.generated.resources.setup_milk
import saltpepper.core.ui.generated.resources.setup_molluscs
import saltpepper.core.ui.generated.resources.setup_mustard
import saltpepper.core.ui.generated.resources.setup_no_alcohol
import saltpepper.core.ui.generated.resources.setup_peanuts
import saltpepper.core.ui.generated.resources.setup_pescatarian
import saltpepper.core.ui.generated.resources.setup_pregnancy
import saltpepper.core.ui.generated.resources.setup_sesame
import saltpepper.core.ui.generated.resources.setup_shellfish
import saltpepper.core.ui.generated.resources.setup_south_american
import saltpepper.core.ui.generated.resources.setup_soy
import saltpepper.core.ui.generated.resources.setup_sulphites
import saltpepper.core.ui.generated.resources.setup_thai
import saltpepper.core.ui.generated.resources.setup_tree_nuts
import saltpepper.core.ui.generated.resources.setup_vegan
import saltpepper.core.ui.generated.resources.setup_vegetarian
import saltpepper.core.ui.generated.resources.setup_vietnamese
import saltpepper.core.ui.generated.resources.setup_vitamins

enum class SetupGroup { DIET, HEALTH, ALLERGY, CUISINE }

// UI choices only: these names are not backend enum values.
enum class SetupOption(val group: SetupGroup, val label: StringResource) {
    VEGAN(SetupGroup.DIET, Res.string.setup_vegan),
    VEGETARIAN(SetupGroup.DIET, Res.string.setup_vegetarian),
    PESCATARIAN(SetupGroup.DIET, Res.string.setup_pescatarian),
    HALAL(SetupGroup.DIET, Res.string.setup_halal),
    NO_ALCOHOL(SetupGroup.DIET, Res.string.setup_no_alcohol),
    LOW_SUGAR(SetupGroup.HEALTH, Res.string.setup_low_sugar),
    ANTI_INFLAMMATORY(SetupGroup.HEALTH, Res.string.setup_anti_inflammatory),
    CHOLESTEROL(SetupGroup.HEALTH, Res.string.setup_cholesterol),
    PREGNANCY(SetupGroup.HEALTH, Res.string.setup_pregnancy),
    HIGH_PROTEIN(SetupGroup.HEALTH, Res.string.setup_high_protein),
    VITAMINS(SetupGroup.HEALTH, Res.string.setup_vitamins),
    IRON(SetupGroup.HEALTH, Res.string.setup_iron),
    GLUTEN(SetupGroup.ALLERGY, Res.string.setup_gluten),
    MILK(SetupGroup.ALLERGY, Res.string.setup_milk),
    EGG(SetupGroup.ALLERGY, Res.string.setup_egg),
    SOY(SetupGroup.ALLERGY, Res.string.setup_soy),
    PEANUTS(SetupGroup.ALLERGY, Res.string.setup_peanuts),
    TREE_NUTS(SetupGroup.ALLERGY, Res.string.setup_tree_nuts),
    FISH(SetupGroup.ALLERGY, Res.string.setup_fish),
    SHELLFISH(SetupGroup.ALLERGY, Res.string.setup_shellfish),
    SESAME(SetupGroup.ALLERGY, Res.string.setup_sesame),
    MUSTARD(SetupGroup.ALLERGY, Res.string.setup_mustard),
    CELERY(SetupGroup.ALLERGY, Res.string.setup_celery),
    SULPHITES(SetupGroup.ALLERGY, Res.string.setup_sulphites),
    LUPIN(SetupGroup.ALLERGY, Res.string.setup_lupin),
    MOLLUSCS(SetupGroup.ALLERGY, Res.string.setup_molluscs),
    FRENCH(SetupGroup.CUISINE, Res.string.setup_french),
    MEDITERRANEAN(SetupGroup.CUISINE, Res.string.setup_mediterranean),
    SOUTH_AMERICAN(SetupGroup.CUISINE, Res.string.setup_south_american),
    VIETNAMESE(SetupGroup.CUISINE, Res.string.setup_vietnamese),
    JAPANESE(SetupGroup.CUISINE, Res.string.setup_japanese),
    CHINESE(SetupGroup.CUISINE, Res.string.setup_chinese),
    ITALIAN(SetupGroup.CUISINE, Res.string.setup_italian),
    INDIAN(SetupGroup.CUISINE, Res.string.setup_indian),
    THAI(SetupGroup.CUISINE, Res.string.setup_thai),
    MEXICAN(SetupGroup.CUISINE, Res.string.setup_mexican),
    MIDDLE_EASTERN(SetupGroup.CUISINE, Res.string.setup_middle_eastern)
}

data class ProfileSetupUiState(
    val step: Int = 0,
    val adults: Int = 1,
    val children: Int = 0,
    val selections: Set<SetupOption> = emptySet()
)

class ProfileSetupViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState = _uiState.asStateFlow()

    fun start() {
        _uiState.update { it.copy(step = 0) }
    }

    fun reset() {
        _uiState.value = ProfileSetupUiState()
    }

    fun next() {
        _uiState.update { it.copy(step = (it.step + 1).coerceAtMost(2)) }
    }

    fun back() {
        _uiState.update { it.copy(step = (it.step - 1).coerceAtLeast(0)) }
    }

    fun changeAdults(increase: Boolean) {
        _uiState.update { it.copy(adults = adjustCount(it.adults, increase, 1)) }
    }

    fun changeChildren(increase: Boolean) {
        _uiState.update { it.copy(children = adjustCount(it.children, increase, 0)) }
    }

    fun toggle(option: SetupOption) {
        _uiState.update {
            it.copy(selections = if (option in it.selections) it.selections - option else it.selections + option)
        }
    }

    private fun adjustCount(value: Int, increase: Boolean, minimum: Int): Int =
        if (increase) {
            if (value < Int.MAX_VALUE) value + 1 else value
        } else {
            (value - 1).coerceAtLeast(minimum)
        }
}
