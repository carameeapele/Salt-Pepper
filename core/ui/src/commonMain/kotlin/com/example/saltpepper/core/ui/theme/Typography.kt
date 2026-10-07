package com.example.saltpepper.core.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import saltpepper.core.ui.generated.resources.NanumPenScript_Regular
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.RobotoMono_Bold
import saltpepper.core.ui.generated.resources.RobotoMono_Light
import saltpepper.core.ui.generated.resources.RobotoMono_Medium
import saltpepper.core.ui.generated.resources.RobotoMono_Regular
import saltpepper.core.ui.generated.resources.RobotoMono_SemiBold
import saltpepper.core.ui.generated.resources.RobotoMono_Thin
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun nanumFont() = FontFamily(
    Font(Res.font.NanumPenScript_Regular , weight = FontWeight.Normal)
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
    val nanumFont = nanumFont()

    copy(
        displayLarge = displayLarge.copy(
            fontFamily = nanumFont,
            fontWeight = FontWeight.Bold,
            fontSize = 46.sp
        ),
        displayMedium = displayMedium.copy(
            fontFamily = robotoFont,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp
        ),
        displaySmall = displaySmall.copy(fontFamily = robotoFont),
        headlineLarge = headlineLarge.copy(fontFamily = robotoFont),
        headlineMedium = headlineMedium.copy(fontFamily = robotoFont),
        headlineSmall = headlineSmall.copy(fontFamily = robotoFont),
        titleLarge = titleLarge.copy(
            fontFamily = robotoFont,
            fontSize = 18.sp
            ),
        titleLargeEmphasized = titleLargeEmphasized.copy(
            fontFamily = robotoFont,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        ),
        titleMedium = titleMedium.copy(
            fontFamily = robotoFont,
            fontSize = 16.sp
            ),
        titleMediumEmphasized = titleMedium.copy(
            fontFamily = robotoFont,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ),
        titleSmall = titleSmall.copy(fontFamily = robotoFont),
        bodyLarge = bodyLarge.copy(fontFamily =  robotoFont),
        bodyLargeEmphasized = bodyLargeEmphasized.copy(
            fontFamily = robotoFont,
            fontWeight = FontWeight.Bold
        ),
        bodyMedium = bodyMedium.copy(fontFamily = robotoFont),
        bodySmall = bodySmall.copy(fontFamily = robotoFont),
        labelLarge = labelLarge.copy(fontFamily = robotoFont),
        labelMedium = labelMedium.copy(fontFamily = robotoFont),
        labelSmall = labelSmall.copy(fontFamily = robotoFont)
    )
}