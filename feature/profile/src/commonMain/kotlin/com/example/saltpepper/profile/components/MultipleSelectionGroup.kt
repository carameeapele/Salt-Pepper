package com.example.saltpepper.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.saltpepper.profile.viewmodel.HouseholdSetupAfterRegistrationUiState
import com.example.saltpepper.profile.viewmodel.SetupGroup
import com.example.saltpepper.profile.viewmodel.SetupOption
import org.jetbrains.compose.resources.stringResource

@Composable
fun MultipleSelectionGroup(
    title: String,
    group: SetupGroup,
    state: HouseholdSetupAfterRegistrationUiState,
    onToggle: (SetupOption) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SetupOption.entries.filter { it.group == group }.forEach { option ->
                FilterChip(
                    selected = option in state.selections,
                    onClick = { onToggle(option) },
                    label = { Text(stringResource(option.label)) }
                )
            }
        }
    }
}