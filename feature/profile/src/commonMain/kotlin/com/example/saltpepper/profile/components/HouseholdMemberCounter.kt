package com.example.saltpepper.profile.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import org.jetbrains.compose.resources.stringResource
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.household_setup_counter_decrease
import saltpepper.core.ui.generated.resources.household_setup_counter_increase

@Composable
fun HouseholdMemberCounter(
    modifier: Modifier = Modifier,
    label: String,
    count: Int,
    minimum: Int,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium
        )
        IconButton(
            onClick = { onChange(false) },
            enabled = count > minimum
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = stringResource(Res.string.household_setup_counter_decrease, label)
            )
        }
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge
        )
        IconButton(
            onClick = { onChange(true) },
            enabled = count < Int.MAX_VALUE
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.household_setup_counter_increase, label)
            )
        }
    }
}

@Preview
@Composable
private fun HouseholdMemberCounterPreview() {
    SaltPepperTheme {
        HouseholdMemberCounter(
            label = "Adults",
            count = 1,
            minimum = 1,
            onChange = { }
        )
    }
}