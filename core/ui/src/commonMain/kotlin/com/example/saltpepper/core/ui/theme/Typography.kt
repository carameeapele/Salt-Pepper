package com.example.saltpepper.core.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import saltpepper.core.ui.generated.resources.Geist_VariableFont_wght
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.RobotoMono_Bold
import saltpepper.core.ui.generated.resources.RobotoMono_Light
import saltpepper.core.ui.generated.resources.RobotoMono_Medium
import saltpepper.core.ui.generated.resources.RobotoMono_Regular
import saltpepper.core.ui.generated.resources.RobotoMono_SemiBold
import saltpepper.core.ui.generated.resources.RobotoMono_Thin
import saltpepper.core.ui.generated.resources.Unbounded_VariableFont_wght

@Composable
private fun unboundedFont() = FontFamily(
    Font(Res.font.Unbounded_VariableFont_wght)
)

@Composable
private fun geistFont() = FontFamily(
    Font(Res.font.Geist_VariableFont_wght)
)

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun robotoMonoFont() = FontFamily(
    Font(Res.font.RobotoMono_Light  , weight = FontWeight.Light),
    Font(Res.font.RobotoMono_Regular , weight = FontWeight.Normal),
    Font(Res.font.RobotoMono_Medium , weight = FontWeight.Medium),
    Font(Res.font.RobotoMono_SemiBold , weight = FontWeight.SemiBold),
    Font(Res.font.RobotoMono_Bold , weight = FontWeight.Bold),
    Font(Res.font.RobotoMono_Thin , weight = FontWeight.Thin),
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SaltPepperTypography() = Typography().run {
    val robotoFont = robotoMonoFont()
    val unboundedFont = unboundedFont()
    val geistFont = geistFont()

    copy(
        displayLarge = displayLarge.copy(
            fontFamily = unboundedFont,
            fontSize = 46.sp,
            fontWeight = FontWeight.Bold
        ),
        displayMedium = displayMedium.copy(
            fontFamily = unboundedFont,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp
        ),
        displaySmall = displaySmall.copy(
            fontFamily = robotoFont,
            fontSize = 36.sp
        ),
        headlineLarge = headlineLarge.copy(
            fontFamily = unboundedFont,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
        ),
        headlineMedium = headlineMedium.copy(
            fontFamily = unboundedFont,
            fontSize = 28.sp
        ),
        headlineSmall = headlineSmall.copy(
            fontFamily = unboundedFont,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        ),
        titleLarge = titleLarge.copy(
            fontFamily = geistFont,
            fontSize = 22.sp
        ),
        titleLargeEmphasized = titleLargeEmphasized.copy(
            fontFamily = geistFont,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        ),
        titleMedium = titleMedium.copy(
            fontFamily = geistFont,
            fontSize = 16.sp
        ),
        titleMediumEmphasized = titleMedium.copy(
            fontFamily = geistFont,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        ),
        titleSmall = titleSmall.copy(
            fontFamily = unboundedFont,
            fontSize = 14.sp
        ),
        bodyLarge = bodyLarge.copy(
            fontFamily =  geistFont,
            fontSize = 16.sp
        ),
        bodyLargeEmphasized = bodyLargeEmphasized.copy(
            fontFamily = geistFont,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        ),
        bodyMedium = bodyMedium.copy(
            fontFamily = geistFont,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        ),
        bodySmall = bodySmall.copy(
            fontFamily = geistFont,
            fontSize = 12.sp
        ),
        labelLarge = labelLarge.copy(
            fontFamily = geistFont,
            fontSize = 14.sp
        ),
        labelMedium = labelMedium.copy(
            fontFamily = robotoFont,
            fontSize = 12.sp
        ),
        labelSmall = labelSmall.copy(
            fontFamily = geistFont,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp
        )
    )
}