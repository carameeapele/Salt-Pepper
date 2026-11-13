package com.example.saltpepper.profile.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.saltpepper.core.ui.component.button.ButtonVariant
import com.example.saltpepper.core.ui.component.button.CustomTextButton
import com.example.saltpepper.core.ui.component.button.PrimaryButton
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import com.example.saltpepper.profile.components.HouseholdMemberCounter
import com.example.saltpepper.profile.components.MultipleSelectionGroup
import com.example.saltpepper.profile.viewmodel.HouseholdSetupAfterRegistrationUiState
import com.example.saltpepper.profile.viewmodel.HouseholdSetupAfterRegistrationViewModel
import com.example.saltpepper.profile.viewmodel.SetupGroup
import com.example.saltpepper.profile.viewmodel.SetupOption
import org.jetbrains.compose.resources.stringResource
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.back
import saltpepper.core.ui.generated.resources.household_setup_counter_adults
import saltpepper.core.ui.generated.resources.household_setup_counter_children
import saltpepper.core.ui.generated.resources.setup_allergies
import saltpepper.core.ui.generated.resources.setup_cuisines
import saltpepper.core.ui.generated.resources.setup_diet_title
import saltpepper.core.ui.generated.resources.setup_diets
import saltpepper.core.ui.generated.resources.setup_finish
import saltpepper.core.ui.generated.resources.setup_health
import saltpepper.core.ui.generated.resources.setup_household_description
import saltpepper.core.ui.generated.resources.setup_household_title
import saltpepper.core.ui.generated.resources.setup_multiple
import saltpepper.core.ui.generated.resources.setup_next
import saltpepper.core.ui.generated.resources.setup_session_notice
import saltpepper.core.ui.generated.resources.setup_skip
import saltpepper.core.ui.generated.resources.setup_step
import saltpepper.core.ui.generated.resources.setup_taste_title

@Composable
fun HouseholdSetupAfterRegistrationScreen(
    viewModel: HouseholdSetupAfterRegistrationViewModel,
    onExit: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    HouseholdSetupAfterRegistrationContent(
        state = state,
        onExit = onExit,
        onNext = viewModel::next,
        onBack = viewModel::back,
        onAdultsChange = viewModel::changeAdults,
        onChildrenChange = viewModel::changeChildren,
        onToggle = viewModel::toggle
    )
}

@Composable
private fun HouseholdSetupAfterRegistrationContent(
    modifier: Modifier = Modifier,
    state: HouseholdSetupAfterRegistrationUiState,
    onExit: () -> Unit = {},
    onNext: () -> Unit = {},
    onBack: () -> Unit = {},
    onAdultsChange: (Boolean) -> Unit = {},
    onChildrenChange: (Boolean) -> Unit = {},
    onToggle: (SetupOption) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomTextButton(
                buttonVariant = ButtonVariant.MEDIUM,
                text = stringResource(Res.string.back),
                onClick = if (state.step == 0) onExit else onBack
            )
            CustomTextButton(
                buttonVariant = ButtonVariant.MEDIUM,
                text = stringResource(Res.string.setup_skip),
                onClick = onExit
            )
        }
        Text(
            text = stringResource(Res.string.setup_step, state.step + 1, 3)
        )
        LinearProgressIndicator(
            progress = { (state.step + 1) / 3f },
            modifier = modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(
                when (state.step) {
                    0 -> Res.string.setup_household_title
                    1 -> Res.string.setup_diet_title
                    else -> Res.string.setup_taste_title
                }
            ),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            stringResource(Res.string.setup_session_notice),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        key(state.step) {
            Column(
                modifier = modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                when (state.step) {
                    0 -> {
                        Text(
                            text = stringResource(Res.string.setup_household_description)
                        )
                        HouseholdMemberCounter(
                            label = stringResource(Res.string.household_setup_counter_adults),
                            count = state.adults,
                            minimum = 1,
                            onChange = onAdultsChange
                        )
                        HouseholdMemberCounter(
                            label = stringResource(Res.string.household_setup_counter_children),
                            count = state.children,
                            minimum = 0,
                            onChange = onChildrenChange
                        )
                    }
                    1 -> {
                        Text(
                            text = stringResource(Res.string.setup_multiple)
                        )
                        MultipleSelectionGroup(
                            title = stringResource(Res.string.setup_diets),
                            group = SetupGroup.DIET,
                            state = state,
                            onToggle = onToggle
                        )
                        MultipleSelectionGroup(
                            title = stringResource(Res.string.setup_health),
                            group = SetupGroup.HEALTH,
                            state = state,
                            onToggle = onToggle
                        )
                        MultipleSelectionGroup(
                            title = stringResource(Res.string.setup_allergies),
                            group = SetupGroup.ALLERGY,
                            state = state,
                            onToggle = onToggle
                        )
                    }
                    else -> {
                        Text(
                            text = stringResource(Res.string.setup_multiple)
                        )
                        MultipleSelectionGroup(
                            title = stringResource(Res.string.setup_cuisines),
                            group = SetupGroup.CUISINE,
                            state = state,
                            onToggle = onToggle
                        )
                    }
                }
            }
        }
        PrimaryButton(
            modifier = modifier.fillMaxWidth(),
            text = stringResource(
                if (state.step == 2) Res.string.setup_finish
                else Res.string.setup_next
            ),
            onClick = if (state.step == 2) onExit else onNext
        )
    }
}

@Preview
@Composable
private fun HouseholdSetupAfterRegistrationPreview() {
    SaltPepperTheme {
        HouseholdSetupAfterRegistrationContent(
            state = HouseholdSetupAfterRegistrationUiState(
                step = 0,
                adults = 2,
                children = 1
            )
        )
    }
}

@Preview
@Composable
private fun DietSetupAfterRegistrationPreview() {
    SaltPepperTheme {
        HouseholdSetupAfterRegistrationContent(
            state = HouseholdSetupAfterRegistrationUiState(step = 1)
        )
    }
}

@Preview
@Composable
private fun TasteSetupAfterRegistrationPreview() {
    SaltPepperTheme {
        HouseholdSetupAfterRegistrationContent(
            state = HouseholdSetupAfterRegistrationUiState(step = 2)
        )
    }
}
