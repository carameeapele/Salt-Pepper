package com.example.saltpepper.core.ui.util

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.month_april
import saltpepper.core.ui.generated.resources.month_august
import saltpepper.core.ui.generated.resources.month_december
import saltpepper.core.ui.generated.resources.month_february
import saltpepper.core.ui.generated.resources.month_january
import saltpepper.core.ui.generated.resources.month_july
import saltpepper.core.ui.generated.resources.month_june
import saltpepper.core.ui.generated.resources.month_march
import saltpepper.core.ui.generated.resources.month_may
import saltpepper.core.ui.generated.resources.month_november
import saltpepper.core.ui.generated.resources.month_october
import saltpepper.core.ui.generated.resources.month_september
import saltpepper.core.ui.generated.resources.month_year

@Composable
fun formatDate(isoDate: String): String {
    val month = when (isoDate.substring(5, 7).toInt()) {
        1 -> Res.string.month_january
        2 -> Res.string.month_february
        3 -> Res.string.month_march
        4 -> Res.string.month_april
        5 -> Res.string.month_may
        6 -> Res.string.month_june
        7 -> Res.string.month_july
        8 -> Res.string.month_august
        9 -> Res.string.month_september
        10 -> Res.string.month_october
        11 -> Res.string.month_november
        12 -> Res.string.month_december
        else -> error("Invalid membership month in $isoDate")
    }
    return stringResource(Res.string.month_year, stringResource(month), isoDate.substring(0, 4))
}
