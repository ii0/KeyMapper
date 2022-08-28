package io.github.sds100.keymapper.mappings.keymaps

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import io.github.sds100.keymapper.R
import io.github.sds100.keymapper.mappings.keymaps.trigger.ConfigTriggerScreen
import io.github.sds100.keymapper.mappings.keymaps.trigger.ConfigTriggerViewModel
import io.github.sds100.keymapper.util.ui.pagerTabIndicatorOffset
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@Composable
fun ConfigKeyMapScreen(
    modifier: Modifier = Modifier,
    triggerViewModel: ConfigTriggerViewModel,
    navigateBack: () -> Unit,
) {
    ConfigKeyMapScreen(
        modifier = modifier,
        navigateBack = navigateBack,
        triggerScreen = { ConfigTriggerScreen(triggerViewModel) }
    )
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ConfigKeyMapScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    triggerScreen: @Composable () -> Unit,
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val tabTitles = remember { listOf("Trigger", "Actions") }

    Scaffold(modifier = modifier, bottomBar = {
        BottomAppBar(actions = {
            BottomAppBarActions(navigateBack = navigateBack)
        }, floatingActionButton = {

        })
    }, topBar = {
        TabRow(selectedTabIndex = pagerState.currentPage, indicator = { tabPositions ->
            TabRowDefaults.Indicator(Modifier.pagerTabIndicatorOffset(pagerState, tabPositions))
        }) {
            tabTitles.forEachIndexed { index, title ->
                Tab(selected = pagerState.currentPage == index, onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }, text = { Text(title) })
            }
        }
    }) { padding ->
        HorizontalPager(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            count = 2,
            state = pagerState
        ) { page ->
            when (page) {
                0 -> triggerScreen()
            }
        }
    }
}

@Composable
private fun BottomAppBarActions(navigateBack: () -> Unit) {
    var showDismissChangesDialog: Boolean by rememberSaveable { mutableStateOf(false) }

    BackHandler { showDismissChangesDialog = true }

    if (showDismissChangesDialog) {
        AlertDialog(onDismissRequest = { showDismissChangesDialog = false },
            title = { Text(stringResource(R.string.config_key_map_discard_changes_dialog_title)) },
            text = { Text(stringResource(R.string.config_key_map_discard_changes_dialog_message)) },
            confirmButton = {
                TextButton(onClick = navigateBack) {
                    Text(stringResource(R.string.pos_confirm))
                }
            }, dismissButton = {
            TextButton(onClick = { showDismissChangesDialog = false }) {
                Text(stringResource(R.string.neg_cancel))
            }
        })
    }

    IconButton(onClick = { showDismissChangesDialog = true }) {
        Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = stringResource(R.string.config_key_map_back_content_description))
    }

    val uriHandler = LocalUriHandler.current
    val triggerGuideUrl = stringResource(R.string.url_trigger_guide)

    IconButton(onClick = {
        uriHandler.openUri(triggerGuideUrl)
    }) {
        Icon(imageVector = Icons.Outlined.HelpOutline, contentDescription = stringResource(R.string.config_key_map_help_content_description))
    }
}

@Preview(device = Devices.PIXEL_4)
@Composable
private fun Preview() {
    ConfigKeyMapScreen(
        triggerScreen = { }
    )
}