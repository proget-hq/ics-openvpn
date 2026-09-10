package pl.proget.openvpn.presentation.main

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import pl.proget.openvpn.R
import pl.proget.openvpn.presentation.common.CenteredVpnTopBar
import pl.proget.openvpn.presentation.common.MenuAction
import pl.proget.openvpn.presentation.common.Marble
import pl.proget.openvpn.presentation.theme.LocalDimens
import pl.proget.openvpn.presentation.theme.StatusGreen
import pl.proget.openvpn.presentation.theme.StatusOrange

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MainScreenContent(
    uiState: MainUiState,
    onConnectChanged: (Boolean) -> Unit,
    onImportProfileClick: () -> Unit,
    onLogsClick: () -> Unit,
    onAboutClick: () -> Unit,
) {
    val activity = LocalActivity.current
    val widthSizeClass = activity?.let {
        calculateWindowSizeClass(it).widthSizeClass
    }
    val landscape = widthSizeClass == WindowWidthSizeClass.Expanded
    val dimens = LocalDimens.current

    Scaffold(
        topBar = {
            CenteredVpnTopBar(
                titleRes = R.string.app_name,
                actions = listOf(
                    MenuAction.Overflow(R.string.import_vpn_profile, onImportProfileClick),
                    MenuAction.Overflow(R.string.logs, onLogsClick),
                    MenuAction.Overflow(R.string.about, onAboutClick),
                ),
                modifier = Modifier.shadow(dimens.topBarShadowElevation)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
    ) { innerPadding ->
        if (landscape) {
            MainScreenLandscapeContent(uiState, onConnectChanged, Modifier.padding(innerPadding))
        } else {
            MainScreenPortraitContent(uiState, onConnectChanged, Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun MainScreenPortraitContent(
    uiState: MainUiState,
    onConnectChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = LocalDimens.current
    val dark = isSystemInDarkTheme()

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(dimens.logoBandPercent),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(if (dark) R.drawable.logo_white else R.drawable.logo_black),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(horizontal = dimens.screenHorizontalMargin)
                    .fillMaxWidth()
                    .height(dimens.logoHeight),
            )
        }

        BottomBlock(uiState, onConnectChanged)
    }
}

@Composable
fun MainScreenLandscapeContent(
    uiState: MainUiState,
    onConnectChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            BottomBlock(uiState, onConnectChanged)
        }
    }
}

@Composable
private fun BottomBlock(
    uiState: MainUiState,
    onConnectChanged: (Boolean) -> Unit,
) {
    val dimens = LocalDimens.current

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.screenHorizontalMargin)
    ) {
        SectionTitle(stringResource(R.string.status_title))
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            MarbleAndValue(
                marble = uiState.statusMarble,
                value = sessionStatusText(uiState.sessionStatus),
            )

            if (uiState.switchVisible) {
                Box(
                    modifier = Modifier.matchParentSize(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    ScaledSwitch(
                        checked = uiState.switchChecked,
                        enabled = uiState.switchEnabled,
                        track = uiState.switchTrack,
                        onCheckedChange = onConnectChanged,
                    )
                }
            }
        }

        ProgetDivider()

        SectionTitle(stringResource(R.string.connection_title))
        MarbleAndValue(
            marble = uiState.connectionMarble,
            value = connectionText(uiState.connectionStatus, uiState.serverName),
        )

        ProgetDivider()

        SectionTitle(stringResource(R.string.info_title))
        Text(
            text = infoText(uiState.info),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(dimens.screenBottomMargin))
    }
}

@Composable
private fun ProgetDivider() {
    val dimens = LocalDimens.current

    HorizontalDivider(
        thickness = LocalDimens.current.dividerThickness,
        color = MaterialTheme.colorScheme.outlineVariant,
        modifier = Modifier.padding(
            top = dimens.valueToDividerGap,
            bottom = dimens.dividerToTitleGap
        )
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )
    Spacer(Modifier.height(LocalDimens.current.sectionTitleGap))
}

@Composable
fun MarbleAndValue(marble: MarbleColor, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Marble(marble)
        Spacer(Modifier.width(LocalDimens.current.marbleGap))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun ScaledSwitch(
    checked: Boolean,
    enabled: Boolean,
    track: SwitchTrack,
    onCheckedChange: (Boolean) -> Unit,
) {
    val dimens = LocalDimens.current
    val trackColor = when (track) {
        SwitchTrack.Default -> MaterialTheme.colorScheme.primary
        SwitchTrack.Green -> StatusGreen
        SwitchTrack.Orange -> StatusOrange
    }
    val colors = SwitchDefaults.colors(
        checkedTrackColor = trackColor,
        checkedBorderColor = trackColor,
        disabledCheckedTrackColor = trackColor,
        disabledCheckedBorderColor = trackColor,
        disabledCheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
    )
    Box(
        modifier = Modifier
            .requiredSize(dimens.switchWidth, dimens.switchHeight),
        contentAlignment = Alignment.Center,
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.scale(dimens.switchScale),
            enabled = enabled,
            colors = colors,
            thumbContent = if (checked && track == SwitchTrack.Green) {
                { ThumbIcon() }
            } else {
                null
            },
        )
    }
}

@Composable
private fun ThumbIcon() {
    val dimens = LocalDimens.current

    Icon(
        painter = painterResource(R.drawable.ic_baseline_check_24),
        contentDescription = null,
        tint = StatusGreen,
        modifier = Modifier.size(dimens.switchThumbIconSize),
    )
}

@Composable
fun sessionStatusText(status: SessionStatus): String = stringResource(
    when (status) {
        SessionStatus.NotInitiated -> R.string.not_initiated
        SessionStatus.Started -> R.string.started
        SessionStatus.AuthFailed -> R.string.state_auth_failed
    }
)

@Composable
private fun connectionText(status: ConnectionStatus, serverName: String?): String = when (status) {
    ConnectionStatus.NotConnected -> stringResource(R.string.not_connected)
    ConnectionStatus.Connecting -> stringResource(R.string.connecting_to, serverName.toString())
    ConnectionStatus.Connected -> stringResource(R.string.connected_to, serverName.toString())
}

@Composable
private fun infoText(info: InfoMessage): String = stringResource(
    when (info) {
        InfoMessage.NoConfiguration -> R.string.no_configuration
        InfoMessage.ManualConfiguration -> R.string.manual_configuration
        InfoMessage.ConfiguredByEmm -> R.string.configured_by_emm
    }
)
