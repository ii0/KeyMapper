package io.github.sds100.keymapper.mappings.keymaps

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.github.sds100.keymapper.R
import io.github.sds100.keymapper.destinations.ConfigTriggerKeyScreenDestination
import io.github.sds100.keymapper.mappings.keymaps.trigger.*
import io.github.sds100.keymapper.util.ui.CustomDialog
import io.github.sds100.keymapper.util.ui.RadioButtonWithText
import io.github.sds100.keymapper.util.ui.SwitchWithText
import io.github.sds100.keymapper.util.ui.pagerTabIndicatorOffset
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@Composable
fun ConfigKeyMapScreen(
    modifier: Modifier = Modifier,
    viewModel: ConfigKeyMapViewModel2,
    navigator: DestinationsNavigator,
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
                modifier = Modifier.fillMaxHeight(),
                configState = triggerState,
                onRecordTriggerClick = viewModel::onRecordTriggerClick,
                onRemoveTriggerKeyClick = viewModel::onRemoveTriggerKeyClick,
                onFixTriggerErrorClick = viewModel::onFixTriggerErrorClick,
                onMoveTriggerKey = viewModel::onMoveTriggerKey,
                onSelectClickType = viewModel::onSelectClickType,
                onSelectParallelTriggerMode = viewModel::onSelectParallelTriggerMode,
                onSelectSequenceTriggerMode = viewModel::onSelectSequenceTriggerMode,
                onChooseTriggerKeyDeviceClick = viewModel::onChooseTriggerKeyDeviceClick,
                onTriggerKeyOptionsClick = { keyUid ->
                    navigator.navigate(ConfigTriggerKeyScreenDestination)
                    viewModel.onLaunchTriggerKeyOptions(keyUid)
                }
            )
        },
        onConfirmDialog = viewModel::onConfirmDialog,
        onNeverShowDndAccessErrorClick = viewModel::onNeverShowDndAccessErrorClick,
        onSelectTriggerKeyDevice = viewModel::onSelectTriggerKeyDevice
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
    onConfirmDialog: () -> Unit = {},
    onNeverShowDndAccessErrorClick: () -> Unit = {},
    onSelectTriggerKeyDevice: (TriggerKeyDevice) -> Unit = {},
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
                onConfirmClick = onConfirmDialog,
                onNeverShowAgainClick = onNeverShowDndAccessErrorClick
            )
        }
        is ConfigKeyMapDialog.ChooseTriggerKeyDevice -> {
            ChooseTriggerKeyDeviceDialog(
                selectedDevice = dialog.selectedDevice,
                devices = dialog.devices,
                onSelectDevice = onSelectTriggerKeyDevice,
                onConfirm = onConfirmDialog,
                onDismiss = onDismissDialog
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

    Row(Modifier
        .clip(ShapeDefaults.Medium)
        .clickable { uriHandler.openUri(triggerGuideUrl) }
        .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(R.string.action_help),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge
        )
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

@Composable
private fun ChooseTriggerKeyDeviceDialog(
    selectedDevice: TriggerKeyDevice,
    devices: List<TriggerKeyDevice>,
    onSelectDevice: (TriggerKeyDevice) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    CustomDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.dialog_title_choose_device),
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.pos_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.neg_cancel))
            }
        }) {

        LazyColumn {
            items(devices) { device ->
                val text = when (device) {
                    TriggerKeyDevice.Any -> stringResource(R.string.any_device)
                    is TriggerKeyDevice.External -> device.name
                    TriggerKeyDevice.Internal -> stringResource(R.string.this_device)
                }

                RadioButtonWithText(
                    modifier = Modifier.fillMaxWidth(),
                    isSelected = device == selectedDevice,
                    text = { Text(text) },
                    onClick = { onSelectDevice(device) })
            }
        }
    }
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