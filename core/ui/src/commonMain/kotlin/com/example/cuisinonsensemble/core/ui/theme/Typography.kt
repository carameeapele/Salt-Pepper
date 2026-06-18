package ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import cuisinonsensemble.core.ui.generated.resources.NanumPenScript_Regular
import cuisinonsensemble.core.ui.generated.resources.Res
import cuisinonsensemble.core.ui.generated.resources.RobotoMono_Bold
import cuisinonsensemble.core.ui.generated.resources.RobotoMono_Light
import cuisinonsensemble.core.ui.generated.resources.RobotoMono_Medium
import cuisinonsensemble.core.ui.generated.resources.RobotoMono_Regular
import cuisinonsensemble.core.ui.generated.resources.RobotoMono_SemiBold
import cuisinonsensemble.core.ui.generated.resources.RobotoMono_Thin
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

@OptIn(ExperimentalResourceApi::class)
@Composable
fun NanumFont() = FontFamily(
    Font(Res.font.NanumPenScript_Regular , weight = FontWeight.Normal)
)

@OptIn(ExperimentalResourceApi::class)
@Composable
fun RobotoMonoFont() = FontFamily(
    Font(Res.font.RobotoMono_Light  , weight = FontWeight.Light),
    Font(Res.font.RobotoMono_Regular , weight = FontWeight.Normal),
    Font(Res.font.RobotoMono_Medium , weight = FontWeight.Medium),
    Font(Res.font.RobotoMono_SemiBold , weight = FontWeight.SemiBold),
    Font(Res.font.RobotoMono_Bold , weight = FontWeight.Bold),
    Font(Res.font.RobotoMono_Thin , weight = FontWeight.Thin),
)

@Composable
fun NanumTypography() = Typography().run {
    val fontFamily = NanumFont()
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily =  fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily)
    )
}

@Composable
fun RobotoMonoTypography() = Typography().run {
    val fontFamily = RobotoMonoFont()
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily =  fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily)
    )
}