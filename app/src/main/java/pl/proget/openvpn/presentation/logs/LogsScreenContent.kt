package pl.proget.openvpn.presentation.logs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import pl.proget.openvpn.R
import pl.proget.openvpn.logs.LogItem
import pl.proget.openvpn.presentation.common.MenuAction
import pl.proget.openvpn.presentation.common.VpnTopBar
import pl.proget.openvpn.presentation.theme.LocalDimens

@Composable
fun LogsScreenContent(
    uiState: LogsUiState,
    onSendLogsClick: () -> Unit,
    navigateBack: () -> Unit,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex < DISTANCE_FROM_LIST_END
        }
    }
    val isAtBottom by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            lastVisibleIndex >= totalItems - DISTANCE_FROM_LIST_END
        }
    }

    Scaffold(
        topBar = {
            VpnTopBar(
                titleRes = R.string.logs,
                navigationIcon = MenuAction.Navigation(
                    iconRes = R.drawable.ic_baseline_arrow_back_24,
                    onClick = navigateBack,
                ),
                actions = listOf(
                    MenuAction.Icon(
                        titleRes = R.string.send_logs,
                        iconRes = R.drawable.ic_baseline_share_24,
                        onClick = onSendLogsClick,
                    )
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LogsList(
                logs = uiState.logs,
                listState = listState,
            )

            if(!isAtTop) {
                ScrollToTopButton {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
            }

            if(!isAtBottom) {
                ScrollToNewestButton {
                    scope.launch {
                        listState.animateScrollToItem(uiState.logs.lastIndex)
                    }
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun BoxScope.ScrollToTopButton(onClick: () -> Unit) {
    val dimens = LocalDimens.current

    Button(
        onClick = onClick,
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = dimens.mediumPadding),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inverseSurface)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_baseline_arrow_back_24),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.inverseOnSurface,
            modifier = Modifier.size(dimens.scrollIconSize).rotate(90f)
        )

        Spacer(Modifier.width(dimens.smallGap))

        Text(
            text = stringResource(R.string.top),
            color = MaterialTheme.colorScheme.inverseOnSurface
        )
    }
}

@Composable
fun BoxScope.ScrollToNewestButton(onClick: () -> Unit) {
    val dimens = LocalDimens.current

    Button(
        onClick = onClick,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = dimens.mediumPadding),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inverseSurface)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_baseline_arrow_back_24),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.inverseOnSurface,
            modifier = Modifier.size(dimens.scrollIconSize).rotate(-90f)
        )

        Spacer(Modifier.width(dimens.smallGap))

        Text(
            text = stringResource(R.string.newest),
            color = MaterialTheme.colorScheme.inverseOnSurface
        )
    }
}

@Composable
private fun LogsList(
    logs: List<LogItem>,
    listState: LazyListState,
) {
    val dimens = LocalDimens.current

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimens.mediumPadding)
    ) {
        itemsIndexed(logs) { index, item ->
            Text(
                text = item.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimens.smallGap),
            )

            if (index != logs.lastIndex) {
                HorizontalDivider(
                    thickness = dimens.dividerThickness,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
        }
    }
}

private const val DISTANCE_FROM_LIST_END = 5
