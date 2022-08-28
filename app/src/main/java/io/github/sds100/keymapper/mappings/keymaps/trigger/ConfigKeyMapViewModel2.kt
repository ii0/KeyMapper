package io.github.sds100.keymapper.mappings.keymaps.trigger

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.sds100.keymapper.R
import io.github.sds100.keymapper.mappings.ClickType
import io.github.sds100.keymapper.mappings.keymaps.ConfigKeyMapUseCase
import io.github.sds100.keymapper.mappings.keymaps.DisplayKeyMapUseCase
import io.github.sds100.keymapper.mappings.keymaps.KeyMap
import io.github.sds100.keymapper.system.devices.InputDeviceUtils
import io.github.sds100.keymapper.system.keyevents.KeyEventUtils
import io.github.sds100.keymapper.util.Error
import io.github.sds100.keymapper.util.State
import io.github.sds100.keymapper.util.ui.ResourceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigKeyMapViewModel2 @Inject constructor(
    private val configUseCase: ConfigKeyMapUseCase,
    private val displayUseCase: DisplayKeyMapUseCase,
    private val recordTriggerUseCase: RecordTriggerUseCase,
    private val resourceProvider: ResourceProvider,
) : ViewModel() {
    private val recordTriggerState: StateFlow<RecordTriggerState> = recordTriggerUseCase.state
        .stateIn(viewModelScope, SharingStarted.Lazily, RecordTriggerState.Stopped)

    private val keyMapFlow: Flow<KeyMap> = configUseCase.mapping
        .dropWhile { it !is State.Data }
        .map { (it as State.Data).data }

    val state: StateFlow<ConfigTriggerState> = combine(keyMapFlow, recordTriggerState) { keyMap, recordState ->
        ConfigTriggerState(
            keys = buildKeyListItems(keyMap.trigger),
            recordTriggerState = recordState,
            errors = listOf(KeyMapTriggerError.DND_ACCESS_DENIED)
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Lazily, ConfigTriggerState())

    var snackBar: ConfigKeyMapSnackbar by mutableStateOf(ConfigKeyMapSnackbar.NONE)
        private set

    var dialog: ConfigKeyMapDialog by mutableStateOf(ConfigKeyMapDialog.None)

    private val showDeviceDescriptors: StateFlow<Boolean> = displayUseCase.showDeviceDescriptors
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val longPressClickTypeString: String by lazy {
        resourceProvider.getString(R.string.clicktype_long_press)
    }

    private val doublePressClickTypeString: String by lazy {
        resourceProvider.getString(R.string.clicktype_double_press)
    }

    private val midDotString: String by lazy { resourceProvider.getString(R.string.middot) }

    init {
        recordTriggerUseCase.onRecordKey.onEach {
            configUseCase.addTriggerKey(it.keyCode, it.device)
        }.launchIn(viewModelScope)
    }

    fun onSaveClick() {
        configUseCase.save()
    }

    fun loadNewKeyMap() {
        configUseCase.loadNewKeyMap()
    }

    fun loadKeyMap(uid: String) {
        viewModelScope.launch {
            configUseCase.loadKeyMap(uid)
        }
    }

    fun onDismissDialog() {
        dialog = ConfigKeyMapDialog.None
    }

    fun onSnackBarClick(snackBar: ConfigKeyMapSnackbar) {
        this.snackBar = ConfigKeyMapSnackbar.NONE

        when (snackBar) {
            ConfigKeyMapSnackbar.NONE -> {}
            ConfigKeyMapSnackbar.ACCESSIBILITY_SERVICE_CRASHED -> {
                val success = displayUseCase.restartAccessibilityService()

                if (!success) {
                    dialog = ConfigKeyMapDialog.AccessibilitySettingsNotFound
                }
            }
            ConfigKeyMapSnackbar.ACCESSIBILITY_SERVICE_DISABLED -> {
                val success = displayUseCase.startAccessibilityService()

                if (!success) {
                    dialog = ConfigKeyMapDialog.AccessibilitySettingsNotFound
                }
            }
        }
    }

    fun onRecordTriggerClick() {
        viewModelScope.launch {
            val result = if (recordTriggerState.value is RecordTriggerState.Stopped) {
                recordTriggerUseCase.startRecording()
            } else {
                recordTriggerUseCase.stopRecording()
            }

            if (result is Error.AccessibilityServiceCrashed) {
                snackBar = ConfigKeyMapSnackbar.ACCESSIBILITY_SERVICE_CRASHED
            } else if (result is Error.AccessibilityServiceDisabled) {
                snackBar = ConfigKeyMapSnackbar.ACCESSIBILITY_SERVICE_DISABLED
            } else {
                snackBar = ConfigKeyMapSnackbar.NONE
            }
        }
    }

    fun onRemoveTriggerKeyClick(uid: String) {
        configUseCase.removeTriggerKey(uid)
    }

    private fun buildKeyListItems(trigger: KeyMapTrigger): List<TriggerKeyListItem2> {
        return trigger.keys.mapIndexed { index, key ->
            val linkType = if (index == trigger.keys.lastIndex) {
                TriggerKeyLinkType.HIDDEN
            } else {
                when (trigger.mode) {
                    is TriggerMode.Parallel -> TriggerKeyLinkType.PLUS
                    TriggerMode.Sequence -> TriggerKeyLinkType.ARROW
                    TriggerMode.Undefined -> TriggerKeyLinkType.HIDDEN
                }
            }
            TriggerKeyListItem2(
                uid = key.uid,
                description = buildKeyDescription(key),
                extraInfo = buildKeyExtraInfo(key),
                linkType = linkType
            )
        }
    }

    private fun buildKeyExtraInfo(key: TriggerKey): String {
        val deviceName = getTriggerKeyDeviceName(key.device, showDeviceDescriptors.value)
        return buildString {
            append(deviceName)

            if (!key.consumeKeyEvent) {
                append(" $midDotString ${resourceProvider.getString(R.string.flag_dont_override_default_action)}")
            }
        }
    }

    private fun buildKeyDescription(key: TriggerKey): String {
        return buildString {
            val clickTypeString = when (key.clickType) {
                ClickType.SHORT_PRESS -> null
                ClickType.LONG_PRESS -> longPressClickTypeString
                ClickType.DOUBLE_PRESS -> doublePressClickTypeString
            }

            append(KeyEventUtils.keyCodeToString(key.keyCode))

            if (clickTypeString != null) {
                append(" $midDotString ")
                append(clickTypeString)
            }
        }
    }

    private fun getTriggerKeyDeviceName(
        device: TriggerKeyDevice,
        showDeviceDescriptors: Boolean,
    ): String = when (device) {
        is TriggerKeyDevice.Internal -> resourceProvider.getString(R.string.this_device)
        is TriggerKeyDevice.Any -> resourceProvider.getString(R.string.any_device)
        is TriggerKeyDevice.External -> {
            if (showDeviceDescriptors) {
                InputDeviceUtils.appendDeviceDescriptorToName(
                    device.descriptor,
                    device.name
                )
            } else {
                device.name
            }
        }
    }
}

sealed class ConfigKeyMapDialog {
    object None : ConfigKeyMapDialog()
    object AccessibilitySettingsNotFound : ConfigKeyMapDialog()
}

enum class ConfigKeyMapSnackbar {
    NONE, ACCESSIBILITY_SERVICE_CRASHED, ACCESSIBILITY_SERVICE_DISABLED
}

data class ConfigTriggerState(
    val keys: List<TriggerKeyListItem2> = emptyList(),
    val recordTriggerState: RecordTriggerState = RecordTriggerState.Stopped,
    val errors: List<KeyMapTriggerError> = emptyList(),
)