package pl.proget.openvpn.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun progetTypography(): Typography {
    val size = LocalDimens.current.textSize
    val base = TextStyle(
        fontSize = size,
        platformStyle = PlatformTextStyle(includeFontPadding = true),
    )

    return Typography(
        bodyMedium = base,                                    
        titleSmall = base.copy(fontWeight = FontWeight.Bold),  
    )
}
