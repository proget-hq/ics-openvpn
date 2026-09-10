package pl.proget.openvpn.presentation.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import pl.proget.openvpn.R
import pl.proget.openvpn.presentation.theme.LocalDimens
import pl.proget.openvpn.presentation.theme.ProgetTheme

@PreviewLightDark
@Composable
private fun VpnTopBarPreview() {
    ProgetTheme {
        VpnTopBar(
            titleRes = R.string.app_name,
            actions = emptyList()
        )
    }
}

@PreviewLightDark
@Composable
private fun VpnTopBarOverflowPreview() {
    ProgetTheme {
        VpnTopBar(
            titleRes = R.string.app_name,
            actions = listOf(
                MenuAction.Overflow(R.string.import_vpn_profile) {},
                MenuAction.Overflow(R.string.logs) {},
                MenuAction.Overflow(R.string.about) {},
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun CenteredVpnTopBarOverflowPreview() {
    ProgetTheme {
        CenteredVpnTopBar(
            titleRes = R.string.app_name,
            actions = listOf(
                MenuAction.Overflow(R.string.import_vpn_profile) {},
                MenuAction.Overflow(R.string.logs) {},
                MenuAction.Overflow(R.string.about) {},
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun VpnTopBarIconActionPreview() {
    ProgetTheme {
        VpnTopBar(
            titleRes = R.string.logs,
            actions = listOf(
                MenuAction.Icon(
                    R.string.send_logs,
                    R.drawable.ic_baseline_share_24
                ) {}
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun VpnTopBarIconNavigationPreview() {
    ProgetTheme {
        VpnTopBar(
            titleRes = R.string.logs,
            navigationIcon = MenuAction.Navigation(
                R.drawable.ic_baseline_archive_24,
                {}
            ),
            actions = emptyList()
        )
    }
}

@Composable
fun CenteredVpnTopBar(
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier,
    actions: List<MenuAction>,
    navigationIcon: MenuAction.Navigation? = null
) {
    VpnTopBarLayout(
        title = {
            Text(
                text = stringResource(titleRes),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier,
        actions = actions,
        navigationIcon = navigationIcon
    )
}

@Composable
fun VpnTopBar(
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier,
    actions: List<MenuAction>,
    navigationIcon: MenuAction.Navigation? = null
) {
    VpnTopBarLayout(
        title = { Text(stringResource(titleRes)) },
        modifier = modifier,
        actions = actions,
        navigationIcon = navigationIcon
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VpnTopBarLayout(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    actions: List<MenuAction>,
    navigationIcon: MenuAction.Navigation? = null
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = { NavigationIcon(navigationIcon) },
        actions = { TopBarActions(actions) },
        expandedHeight = LocalDimens.current.actionBarHeight,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground,
        )
    )
}

@Composable
private fun NavigationIcon(icon: MenuAction.Navigation?) {
    icon?.let {
        IconButton(
            onClick = icon.onClick
        ) {
            Icon(
                painter = painterResource(icon.iconRes),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun TopBarActions(actions: List<MenuAction>) {
    actions.filterIsInstance<MenuAction.Icon>().forEach { action ->
        IconButton(onClick = action.onClick) {
            Icon(
                painter = painterResource(action.iconRes),
                contentDescription = stringResource(action.titleRes),
            )
        }
    }

    val overflowActions = actions.filterIsInstance<MenuAction.Overflow>()
    if (overflowActions.isNotEmpty()) {
        OverflowMenu(overflowActions)
    }
}

@Composable
private fun OverflowMenu(actions: List<MenuAction.Overflow>) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            painter = painterResource(R.drawable.ic_baseline_more_vert_24),
            contentDescription = stringResource(R.string.menu_more_options),
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        containerColor = MaterialTheme.colorScheme.background
    ) {
        actions.forEach { action ->
            DropdownMenuItem(
                text = { Text(stringResource(action.titleRes)) },
                onClick = {
                    expanded = false
                    action.onClick()
                },
            )
        }
    }
}

sealed interface MenuAction {
    data class Icon(
        @StringRes val titleRes: Int,
        @DrawableRes val iconRes: Int,
        val onClick: () -> Unit,
    ) : MenuAction

    data class Navigation(
        @DrawableRes val iconRes: Int,
        val onClick: () -> Unit,
    ) : MenuAction

    data class Overflow(
        @StringRes val titleRes: Int,
        val onClick: () -> Unit,
    ) : MenuAction
}
