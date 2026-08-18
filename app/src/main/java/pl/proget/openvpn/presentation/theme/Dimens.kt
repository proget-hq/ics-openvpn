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
    val textSize: TextUnit,           
    val actionBarHeight: Dp,
    val toolbarTextSize: TextUnit,
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
    textSize = 14.sp,
    actionBarHeight = 56.dp,
    toolbarTextSize = 20.sp,
)

val LocalDimens = staticCompositionLocalOf { dimens }
