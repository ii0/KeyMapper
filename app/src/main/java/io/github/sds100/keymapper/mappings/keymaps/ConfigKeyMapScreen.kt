package io.github.sds100.keymapper.mappings.keymaps

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import io.github.sds100.keymapper.R
import io.github.sds100.keymapper.mappings.keymaps.trigger.ConfigKeyMapDialog
import io.github.sds100.keymapper.mappings.keymaps.trigger.ConfigKeyMapSnackbar
import io.github.sds100.keymapper.mappings.keymaps.trigger.ConfigKeyMapViewModel2
import io.github.sds100.keymapper.mappings.keymaps.trigger.ConfigTriggerScreen
import io.github.sds100.keymapper.util.ui.SwitchWithText
import io.github.sds100.keymapper.util.ui.pagerTabIndicatorOffset
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@Composable
fun ConfigKeyMapScreen(
    modifier: Modifier = Modifier,
    viewModel: ConfigKeyMapViewModel2,
    navigateBack: () -> Unit,
) {
    val triggerState by viewModel.triggerState.collectAsState()
    val isKeyMapEnabled by viewModel.isKeyMapEnabled.collectAsState()

    ConfigKeyMapScreen(
        modifier = modifier,
        snackbar = viewModel.snackBar,
        dialog = viewModel.dialog,
        isKeyMapEnabled = isKeyMapEnabled,
        onKeyMapEnabledChange = viewModel::onKeyMapEnabledChange,
        navigateBack = navigateBack,
        onSaveClick = {
            viewModel.onSaveClick()
            navigateBack()
        },
        onSnackbarClick = viewModel::onSnackBarClick,
        onDismissDialog = viewModel::onDismissDialog,
        triggerScreen = {
            ConfigTriggerScreen(
                configState = triggerState,
                onRecordTriggerClick = viewModel::onRecordTriggerClick,
                onRemoveTriggerKeyClick = viewModel::onRemoveTriggerKeyClick,
                onFixTriggerErrorClick = viewModel::onFixTriggerErrorClick
            )
        },
        onConfirmDndAccessErrorClick = viewModel::onConfirmDndAccessExplanationClick,
        onNeverShowDndAccessErrorClick = viewModel::onNeverShowDndAccessErrorClick
    )
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ConfigKeyMapScreen(
    modifier: Modifier = Modifier,
    snackbar: ConfigKeyMapSnackbar,
    dialog: ConfigKeyMapDialog,
    isKeyMapEnabled: Boolean,
    onKeyMapEnabledChange: (Boolean) -> Unit = {},
    navigateBack: () -> Unit = {},
    triggerScreen: @Composable () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onSnackbarClick: (ConfigKeyMapSnackbar) -> Unit = {},
    onDismissDialog: () -> Unit = {},
    onConfirmDndAccessErrorClick: () -> Unit = {},
    onNeverShowDndAccessErrorClick: () -> Unit = {},
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val tabTitles = remember { listOf("Trigger", "Actions") }
    val snackbarHostState = remember { SnackbarHostState() }

    when (snackbar) {
        ConfigKeyMapSnackbar.NONE -> {}
        ConfigKeyMapSnackbar.ACCESSIBILITY_SERVICE_CRASHED -> {
            val message = stringResource(R.string.error_accessibility_service_crashed)
            val actionLabel = stringResource(R.string.pos_restart)
            LaunchedEffect(snackbar) {
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                )

                if (result == SnackbarResult.ActionPerformed) {
                    onSnackbarClick(snackbar)
                }
            }
        }

        ConfigKeyMapSnackbar.ACCESSIBILITY_SERVICE_DISABLED -> {
            val message = stringResource(R.string.error_accessibility_service_disabled_record_trigger)
            val actionLabel = stringResource(R.string.enable)

            LaunchedEffect(snackbar) {
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                )

                if (result == SnackbarResult.ActionPerformed) {
                    onSnackbarClick(snackbar)
                }
            }
        }

    }

    when (dialog) {
        ConfigKeyMapDialog.None -> {}
        ConfigKeyMapDialog.AccessibilitySettingsNotFound -> {
            AccessibilitySettingsNotFoundDialog(onDismiss = onDismissDialog)
        }
        ConfigKeyMapDialog.DndAccessExplanation -> {
            DndAccessExplanationDialog(
                onDismiss = onDismissDialog,
                onConfirmClick = onConfirmDndAccessErrorClick,
                onNeverShowAgainClick = onNeverShowDndAccessErrorClick
            )
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomAppBar(actions = {
                BottomAppBarActions(
                    navigateBack = navigateBack,
                    isKeyMapEnabled = isKeyMapEnabled,
                    onKeyMapEnabledChange = onKeyMapEnabledChange
                )
            }, floatingActionButton = {
                FloatingActionButton(
                    onClick = onSaveClick,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor
                ) {
                    Icon(
                        Icons.Outlined.Save,
                        contentDescription = stringResource(R.string.config_key_map_save_button)
                    )
                }
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
private fun BottomAppBarActions(
    navigateBack: () -> Unit,
    isKeyMapEnabled: Boolean,
    onKeyMapEnabledChange: (Boolean) -> Unit,
) {
    var showDismissChangesDialog: Boolean by rememberSaveable { mutableStateOf(false) }

    BackHandler { showDismissChangesDialog = true }

    if (showDismissChangesDialog) {
        AlertDialog(
            onDismissRequest = { showDismissChangesDialog = false },
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
        Icon(
            imageVector = Icons.Outlined.ArrowBack,
            contentDescription = stringResource(R.string.config_key_map_back_content_description)
        )
    }

    val uriHandler = LocalUriHandler.current
    val triggerGuideUrl = stringResource(R.string.url_trigger_guide)

    TextButton(onClick = { uriHandler.openUri(triggerGuideUrl) }) {
        Text(stringResource(R.string.action_help), color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Outlined.HelpOutline,
            contentDescription = stringResource(R.string.config_key_map_help_content_description)
        )
    }

    SwitchWithText(
        checked = isKeyMapEnabled,
        onChange = onKeyMapEnabledChange,
        text = {
            Text(
                stringResource(R.string.switch_enabled),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    )
}

@Composable
private fun DndAccessExplanationDialog(
    onDismiss: () -> Unit,
    onConfirmClick: () -> Unit,
    onNeverShowAgainClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_title_fix_dnd_trigger_error)) },
        text = { Text(stringResource(R.string.dialog_message_fix_dnd_trigger_error)) },
        confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text(stringResource(R.string.pos_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onNeverShowAgainClick) {
                Text(stringResource(R.string.dialog_button_never_show_again))
            }

            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.neg_cancel))
            }
        },
    )
}

@Composable
private fun AccessibilitySettingsNotFoundDialog(
    onDismiss: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val guideUrl = stringResource(R.string.url_cant_find_accessibility_settings_issue)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_title_cant_find_accessibility_settings_page)) },
        text = { Text(stringResource(R.string.dialog_message_cant_find_accessibility_settings_page)) },
        confirmButton = {
            TextButton(onClick = { uriHandler.openUri(guideUrl) }) {
                Text(stringResource(R.string.pos_start_service_with_adb_guide))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.neg_cancel))
            }
        }
    )
}

@Preview(device = Devices.PIXEL_4)
@Composable
private fun Preview() {
    ConfigKeyMapScreen(
        snackbar = ConfigKeyMapSnackbar.ACCESSIBILITY_SERVICE_CRASHED,
        dialog = ConfigKeyMapDialog.None,
        isKeyMapEnabled = true
    )
}