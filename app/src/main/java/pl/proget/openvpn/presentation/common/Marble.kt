package pl.proget.openvpn.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pl.proget.openvpn.presentation.main.MarbleColor
import pl.proget.openvpn.presentation.theme.LocalDimens
import pl.proget.openvpn.presentation.theme.StatusGreen
import pl.proget.openvpn.presentation.theme.StatusOrange
import pl.proget.openvpn.presentation.theme.StatusRed

@Composable
fun Marble(color: MarbleColor) {
    Box(
        Modifier
            .size(LocalDimens.current.marbleSize)
            .background(
                color = when (color) {
                    MarbleColor.Red -> StatusRed
                    MarbleColor.Green -> StatusGreen
                    MarbleColor.Orange -> StatusOrange
                },
                shape = CircleShape,
            )
    )
}
