package io.github.sds100.keymapper.mappings.keymaps.trigger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.sds100.keymapper.R
import io.github.sds100.keymapper.mappings.ClickType
import io.github.sds100.keymapper.mappings.keymaps.ConfigKeyMapUseCase
import io.github.sds100.keymapper.mappings.keymaps.DisplayKeyMapUseCase
import io.github.sds100.keymapper.system.devices.InputDeviceUtils
import io.github.sds100.keymapper.system.keyevents.KeyEventUtils
import io.github.sds100.keymapper.util.State
import io.github.sds100.keymapper.util.ui.ResourceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigTriggerViewModel @Inject constructor(
    private val configUseCase: ConfigKeyMapUseCase,
    private val displayUseCase: DisplayKeyMapUseCase,
    private val recordTriggerUseCase: RecordTriggerUseCase,
    private val resourceProvider: ResourceProvider,
) : ViewModel() {
    val state: StateFlow<ConfigTriggerState> = configUseCase.mapping
        .dropWhile { it !is State.Data }
        .map { (it as State.Data).data }
        .map { keyMap ->
            if (keyMap.trigger.keys.isEmpty()) {
                ConfigTriggerState.Empty
            } else {
                ConfigTriggerState.Trigger(
                    keys = buildKeyListItems(keyMap.trigger)
                )
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Lazily, ConfigTriggerState.Empty)

    private val showDeviceDescriptors: StateFlow<Boolean> = displayUseCase.showDeviceDescriptors
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val longPressClickTypeString: String by lazy {
        resourceProvider.getString(R.string.clicktype_long_press)
    }

    private val doublePressClickTypeString: String by lazy {
        resourceProvider.getString(R.string.clicktype_double_press)
    }

    private val midDotString: String by lazy { resourceProvider.getString(R.string.middot) }

    private val recordTriggerState: StateFlow<RecordTriggerState> = recordTriggerUseCase.state
        .stateIn(viewModelScope, SharingStarted.Lazily, RecordTriggerState.Stopped)

    fun loadNewKeyMap() {
        configUseCase.loadNewKeyMap()
    }

    fun loadKeyMap(uid: String) {
        viewModelScope.launch {
            configUseCase.loadKeyMap(uid)
        }
    }

    fun onRecordTriggerClick() {
        viewModelScope.launch {
            if (recordTriggerState.value is RecordTriggerState.Stopped) {
                recordTriggerUseCase.startRecording()
            } else {
                recordTriggerUseCase.stopRecording()
            }
        }
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

            if (clickTypeString != null) {
                append(clickTypeString)
            }

            append(" $midDotString ")
            append(KeyEventUtils.keyCodeToString(key.keyCode))
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

sealed class ConfigTriggerState {
    object Empty : ConfigTriggerState()
    data class Trigger(val keys: List<TriggerKeyListItem2>) : ConfigTriggerState()
}