package pl.proget.openvpn.presentation.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import pl.proget.openvpn.BuildConfig
import pl.proget.openvpn.R
import pl.proget.openvpn.presentation.common.MenuAction
import pl.proget.openvpn.presentation.common.VpnTopBar
import pl.proget.openvpn.presentation.theme.LocalDimens
import pl.proget.openvpn.presentation.theme.ProgetTheme

@PreviewLightDark
@Composable
private fun AboutScreenPreview() {
    ProgetTheme {
        AboutScreen {}
    }
}

@Composable
fun AboutScreen(
    navigateBack: () -> Unit
) {
    val dimens = LocalDimens.current

    Scaffold(
        topBar = {
            VpnTopBar(
                titleRes = R.string.app_name,
                navigationIcon = MenuAction.Navigation(
                    iconRes = R.drawable.ic_baseline_arrow_back_24,
                    onClick = navigateBack,
                ),
                actions = emptyList()
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.aboutHorizontalMargin)
                .padding(top = dimens.aboutTopMargin),
            verticalArrangement = Arrangement.spacedBy(dimens.aboutSectionGap),
        ) {
            AboutSection(
                title = stringResource(
                    R.string.about_vpn_title,
                    stringResource(R.string.app),
                    BuildConfig.VERSION_NAME,
                ),
                description = stringResource(R.string.vpn_copyright, BuildConfig.VERSION_NAME),
            )
            AboutSection(
                title = stringResource(R.string.openvpn),
                description = stringResource(R.string.copyright_openvpn),
            )
            AboutSection(
                title = stringResource(R.string.file_dialog),
                description = stringResource(R.string.copyright_file_dialog),
            )
            AboutSection(
                title = stringResource(R.string.lzo),
                description = stringResource(R.string.lzo_copyright),
            )
            AboutSection(
                title = stringResource(R.string.openssl),
                description = stringResource(R.string.copyright_openssl),
            )
            AboutSection(
                title = stringResource(R.string.bouncy_castle),
                description = stringResource(R.string.copyright_bouncycastle),
            )
        }
    }
}

@Composable
private fun AboutSection(
    title: String,
    description: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(LocalDimens.current.aboutTitleGap),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
