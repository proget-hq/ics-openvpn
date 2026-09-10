package pl.proget.openvpn.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Dimens(
    val screenHorizontalMargin: Dp,
    val screenBottomMargin: Dp,
    val sectionTitleGap: Dp,
    val valueToDividerGap: Dp,
    val dividerToTitleGap: Dp,
    val dividerThickness: Dp,
    val marbleSize: Dp,
    val marbleGap: Dp,
    val logoHeight: Dp,
    val logoBandPercent: Float,
    val switchWidth: Dp,
    val switchHeight: Dp,
    val switchScale: Float,
    val switchThumbIconSize: Dp,
    val textSize: TextUnit,
    val actionBarHeight: Dp,
    val toolbarTextSize: TextUnit,
    val aboutHorizontalMargin: Dp,
    val aboutTopMargin: Dp,
    val aboutSectionGap: Dp,
    val aboutTitleGap: Dp,
    val aboutTitleTextSize: TextUnit,
    val smallGap: Dp,
    val mediumPadding: Dp,
    val scrollIconSize: Dp,
    val fabBorderRoundnessPercent: Int,
    val topBarShadowElevation: Dp,
)

val dimens = Dimens(
    screenHorizontalMargin = 32.dp,
    screenBottomMargin = 32.dp,
    sectionTitleGap = 12.dp,
    valueToDividerGap = 11.dp,
    dividerToTitleGap = 12.dp,
    dividerThickness = 1.dp,
    marbleSize = 12.dp,
    marbleGap = 12.dp,
    logoHeight = 29.dp,
    logoBandPercent = 0.45f,
    switchWidth = 52.dp,
    switchHeight = 48.dp,
    switchScale = 0.8f,
    switchThumbIconSize = 16.dp,
    textSize = 14.sp,
    actionBarHeight = 56.dp,
    toolbarTextSize = 20.sp,
    aboutHorizontalMargin = 8.dp,
    aboutTopMargin = 4.dp,
    aboutSectionGap = 12.dp,
    aboutTitleGap = 4.dp,
    aboutTitleTextSize = 16.sp,
    smallGap = 6.dp,
    mediumPadding = 12.dp,
    scrollIconSize = 20.dp,
    fabBorderRoundnessPercent = 30,
    topBarShadowElevation = 4.dp
)

val LocalDimens = staticCompositionLocalOf { dimens }
