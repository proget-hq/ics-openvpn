package pl.proget.openvpn.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColors = lightColorScheme(
    primary = ProgetPlum,
    onPrimary = White,
    background = White,            
    onBackground = Black,
    surface = White,
    onSurface = Black,             
    outlineVariant = Grey,
)

private val DarkColors = darkColorScheme(
    primary = ProgetPlum,
    onPrimary = White,
    background = Black,
    onBackground = White,
    surface = Black,
    onSurface = White,
    outlineVariant = DarkGray,
)

@Composable
fun ProgetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalDimens provides dimens) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = progetTypography(),
            content = content,
        )
    }
}
