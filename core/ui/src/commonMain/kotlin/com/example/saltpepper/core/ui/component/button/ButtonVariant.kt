package com.example.saltpepper.core.ui.component.button

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ButtonVariant(
    val height: Dp,
    val textOnlyHeight: Dp,
    val textSize: TextUnit,
    val iconSize: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val cornerRadius: Dp
) {
    GIANT(
        height = 56.dp,
        textOnlyHeight = 36.dp,
        textSize = 20.sp,
        iconSize = 24.dp,
        horizontalPadding = 24.dp,
        verticalPadding = 16.dp,
        cornerRadius = 12.dp
    ),
    LARGE(
        height = 48.dp,
        textOnlyHeight = 32.dp,
        textSize = 18.sp,
        iconSize = 22.dp,
        horizontalPadding = 20.dp,
        verticalPadding = 14.dp,
        cornerRadius = 12.dp
    ),
    MEDIUM(
        height = 40.dp,
        textOnlyHeight = 28.dp,
        textSize = 16.sp,
        iconSize = 20.dp,
        horizontalPadding = 16.dp,
        verticalPadding = 12.dp,
        cornerRadius = 10.dp
    ),
    SMALL(
        height = 32.dp,
        textOnlyHeight = 24.dp,
        textSize = 13.sp,
        iconSize = 18.dp,
        horizontalPadding = 12.dp,
        verticalPadding = 8.dp,
        cornerRadius = 8.dp
    ),
    TINY(
        height = 24.dp,
        textOnlyHeight = 16.dp,
        textSize = 10.sp,
        iconSize = 12.dp,
        horizontalPadding = 8.dp,
        verticalPadding = 6.dp,
        cornerRadius = 6.dp
    )
}