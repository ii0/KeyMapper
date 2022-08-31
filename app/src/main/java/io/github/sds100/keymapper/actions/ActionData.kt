package io.github.sds100.keymapper.actions

import android.os.Parcelable
import io.github.sds100.keymapper.system.camera.CameraLens
import io.github.sds100.keymapper.system.display.Orientation
import io.github.sds100.keymapper.system.intents.IntentTarget
import io.github.sds100.keymapper.system.volume.DndMode
import io.github.sds100.keymapper.system.volume.RingerMode
import io.github.sds100.keymapper.system.volume.VolumeStream
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed class ActionData : Parcelable {
    abstract val id: ActionId

    @Serializable
    @Parcelize
    data class App(
        val packageName: String,
    ) : ActionData() {
        override val id: ActionId = ActionId.APP
    }

    @Serializable
    @Parcelize
    data class AppShortcut(
        val packageName: String?,
        val shortcutTitle: String,
        val uri: String,
    ) : ActionData() {
        override val id: ActionId = ActionId.APP_SHORTCUT
    }

    @Serializable
    @Parcelize
    data class InputKeyEvent(
        val keyCode: Int,
        val metaState: Int = 0,
        val useShell: Boolean = false,
        val device: Device? = null,
    ) : ActionData(), Parcelable {

        override val id: ActionId = ActionId.KEY_EVENT

        @Serializable
        @Parcelize
        data class Device(
            val descriptor: String,
            val name: String,
        ) : Parcelable
    }

    @Serializable
    @Parcelize
    data class Sound(
        val soundUid: String,
        val soundDescription: String,
    ) : ActionData() {
        override val id = ActionId.SOUND
    }

    @Serializable
    @Parcelize
    sealed class Volume : ActionData() {

        abstract val volumeStream: VolumeStream
        abstract val showVolumeUi: Boolean

        @Serializable
        @Parcelize
        data class Up(override val showVolumeUi: Boolean, override val volumeStream: VolumeStream) :
            Volume() {
            override val id = ActionId.VOLUME_UP
        }

        @Serializable
        @Parcelize
        data class Down(
            override val showVolumeUi: Boolean,
            override val volumeStream: VolumeStream,
        ) : Volume() {
            override val id = ActionId.VOLUME_DOWN
        }

        @Serializable
        @Parcelize
        data class Mute(
            override val showVolumeUi: Boolean,
            override val volumeStream: VolumeStream,
        ) : Volume() {
            override val id = ActionId.VOLUME_MUTE
        }

        @Serializable
        @Parcelize
        data class UnMute(
            override val showVolumeUi: Boolean,
            override val volumeStream: VolumeStream,
        ) : Volume() {
            override val id = ActionId.VOLUME_UNMUTE
        }

        @Serializable
        @Parcelize
        data class ToggleMute(
            override val showVolumeUi: Boolean,
            override val volumeStream: VolumeStream,
        ) : Volume() {
            override val id = ActionId.VOLUME_TOGGLE_MUTE
        }
    }

    @Serializable
    @Parcelize
    data class SetRingerMode(
        val ringerMode: RingerMode,
    ) : ActionData() {
        override val id: ActionId = ActionId.CHANGE_RINGER_MODE
    }

    @Serializable
    @Parcelize
    object ShowVolumeDialog : ActionData() {
        override val id = ActionId.VOLUME_SHOW_DIALOG
    }

    @Serializable
    @Parcelize
    object CycleRingerMode : ActionData() {
        override val id = ActionId.CYCLE_RINGER_MODE
    }

    @Serializable
    @Parcelize
    object CycleVibrateRing : ActionData() {
        override val id = ActionId.CYCLE_VIBRATE_RING
    }

    @Serializable
    @Parcelize
    sealed class Flashlight : ActionData() {
        abstract val lens: CameraLens

        @Serializable
        @Parcelize
        data class Toggle(override val lens: CameraLens) : Flashlight() {
            override val id = ActionId.TOGGLE_FLASHLIGHT
        }

        @Serializable
        @Parcelize
        data class Enable(override val lens: CameraLens) : Flashlight() {
            override val id = ActionId.ENABLE_FLASHLIGHT
        }

        @Serializable
        @Parcelize
        data class Disable(override val lens: CameraLens) : Flashlight() {
            override val id = ActionId.DISABLE_FLASHLIGHT
        }
    }

    @Serializable
    @Parcelize
    data class SwitchKeyboard(
        val imeId: String,
        val savedImeName: String,
    ) : ActionData() {
        override val id = ActionId.SWITCH_KEYBOARD
    }

    @Serializable
    @Parcelize
    sealed class DoNotDisturb : ActionData() {

        @Serializable
        @Parcelize
        data class Toggle(val dndMode: DndMode) : DoNotDisturb() {
            override val id: ActionId = ActionId.TOGGLE_DND_MODE
        }

        @Serializable
        @Parcelize
        data class Enable(val dndMode: DndMode) : DoNotDisturb() {
            override val id: ActionId = ActionId.ENABLE_DND_MODE
        }

        @Serializable
        @Parcelize
        object Disable : DoNotDisturb() {
            override val id = ActionId.DISABLE_DND_MODE
        }
    }

    @Serializable
    @Parcelize
    sealed class Rotation : ActionData() {
        @Serializable
        @Parcelize
        object EnableAuto : Rotation() {
            override val id = ActionId.ENABLE_AUTO_ROTATE
        }

        @Serializable
        @Parcelize
        object DisableAuto : Rotation() {
            override val id = ActionId.DISABLE_AUTO_ROTATE
        }

        @Serializable
        @Parcelize
        object ToggleAuto : Rotation() {
            override val id = ActionId.TOGGLE_AUTO_ROTATE
        }

        @Serializable
        @Parcelize
        object Portrait : Rotation() {
            override val id = ActionId.PORTRAIT_MODE
        }

        @Serializable
        @Parcelize
        object Landscape : Rotation() {
            override val id = ActionId.LANDSCAPE_MODE
        }

        @Serializable
        @Parcelize
        object SwitchOrientation : Rotation() {
            override val id = ActionId.SWITCH_ORIENTATION
        }

        @Serializable
        @Parcelize
        data class CycleRotations(
            val orientations: List<Orientation>,
        ) : Rotation() {
            override val id = ActionId.CYCLE_ROTATIONS
        }
    }

    @Serializable
    @Parcelize
    sealed class ControlMediaForApp : ActionData() {
        abstract val packageName: String

        @Serializable
        @Parcelize
        data class Pause(override val packageName: String) : ControlMediaForApp() {
            override val id = ActionId.PAUSE_MEDIA_PACKAGE
        }

        @Serializable
        @Parcelize
        data class Play(override val packageName: String) : ControlMediaForApp() {
            override val id = ActionId.PLAY_MEDIA_PACKAGE
        }

        @Serializable
        @Parcelize
        data class PlayPause(override val packageName: String) : ControlMediaForApp() {
            override val id = ActionId.PLAY_PAUSE_MEDIA_PACKAGE
        }

        @Serializable
        @Parcelize
        data class NextTrack(override val packageName: String) : ControlMediaForApp() {
            override val id = ActionId.NEXT_TRACK_PACKAGE
        }

        @Serializable
        @Parcelize
        data class PreviousTrack(override val packageName: String) : ControlMediaForApp() {
            override val id = ActionId.PREVIOUS_TRACK_PACKAGE
        }

        @Serializable
        @Parcelize
        data class FastForward(override val packageName: String) : ControlMediaForApp() {
            override val id = ActionId.FAST_FORWARD_PACKAGE
        }

        @Serializable
        @Parcelize
        data class Rewind(override val packageName: String) : ControlMediaForApp() {
            override val id = ActionId.REWIND_PACKAGE
        }
    }

    @Serializable
    @Parcelize
    sealed class ControlMedia : ActionData() {
        @Serializable
        @Parcelize
        object Pause : ControlMedia() {
            override val id = ActionId.PAUSE_MEDIA
        }

        @Serializable
        @Parcelize
        object Play : ControlMedia() {
            override val id = ActionId.PLAY_MEDIA
        }

        @Serializable
        @Parcelize
        object PlayPause : ControlMedia() {
            override val id = ActionId.PLAY_PAUSE_MEDIA
        }

        @Serializable
        @Parcelize
        object NextTrack : ControlMedia() {
            override val id = ActionId.NEXT_TRACK
        }

        @Serializable
        @Parcelize
        object PreviousTrack : ControlMedia() {
            override val id = ActionId.PREVIOUS_TRACK
        }

        @Serializable
        @Parcelize
        object FastForward : ControlMedia() {
            override val id = ActionId.FAST_FORWARD
        }

        @Serializable
        @Parcelize
        object Rewind : ControlMedia() {
            override val id = ActionId.REWIND
        }
    }

    @Serializable
    @Parcelize
    data class Intent(
        val description: String,
        val target: IntentTarget,
        val uri: String,
    ) : ActionData() {
        override val id = ActionId.INTENT
    }

    @Serializable
    @Parcelize
    data class TapScreen(
        val x: Int,
        val y: Int,
        val description: String?,
    ) : ActionData() {
        override val id = ActionId.TAP_SCREEN
    }

    @Serializable
    @Parcelize
    data class PhoneCall(
        val number: String,
    ) : ActionData() {
        override val id = ActionId.PHONE_CALL
    }

    @Serializable
    @Parcelize
    data class Url(
        val url: String,
    ) : ActionData() {
        override val id = ActionId.URL
    }

    @Serializable
    @Parcelize
    data class Text(
        val text: String,
    ) : ActionData() {
        override val id = ActionId.TEXT
    }

    @Serializable
    @Parcelize
    sealed class Wifi : ActionData() {
        @Serializable
        @Parcelize
        object Enable : Wifi() {
            override val id = ActionId.ENABLE_WIFI
        }

        @Serializable
        @Parcelize
        object Disable : Wifi() {
            override val id = ActionId.DISABLE_WIFI
        }

        @Serializable
        @Parcelize
        object Toggle : Wifi() {
            override val id = ActionId.TOGGLE_WIFI
        }
    }

    @Serializable
    @Parcelize
    sealed class Bluetooth : ActionData() {
        @Serializable
        @Parcelize
        object Enable : Bluetooth() {
            override val id = ActionId.ENABLE_BLUETOOTH
        }

        @Serializable
        @Parcelize
        object Disable : Bluetooth() {
            override val id = ActionId.DISABLE_BLUETOOTH
        }

        @Serializable
        @Parcelize
        object Toggle : Bluetooth() {
            override val id = ActionId.TOGGLE_BLUETOOTH
        }
    }

    @Serializable
    @Parcelize
    sealed class Nfc : ActionData() {
        @Serializable
        @Parcelize
        object Enable : Nfc() {
            override val id = ActionId.ENABLE_NFC
        }

        @Serializable
        @Parcelize
        object Disable : Nfc() {
            override val id = ActionId.DISABLE_NFC
        }

        @Serializable
        @Parcelize
        object Toggle : Nfc() {
            override val id = ActionId.TOGGLE_NFC
        }
    }

    @Serializable
    @Parcelize
    sealed class AirplaneMode : ActionData() {
        @Serializable
        @Parcelize
        object Enable : AirplaneMode() {
            override val id = ActionId.ENABLE_AIRPLANE_MODE
        }

        @Serializable
        @Parcelize
        object Disable : AirplaneMode() {
            override val id = ActionId.DISABLE_AIRPLANE_MODE
        }

        @Serializable
        @Parcelize
        object Toggle : AirplaneMode() {
            override val id = ActionId.TOGGLE_AIRPLANE_MODE
        }
    }

    @Serializable
    @Parcelize
    sealed class MobileData : ActionData() {
        @Serializable
        @Parcelize
        object Enable : MobileData() {
            override val id = ActionId.ENABLE_MOBILE_DATA
        }

        @Serializable
        @Parcelize
        object Disable : MobileData() {
            override val id = ActionId.DISABLE_MOBILE_DATA
        }

        @Serializable
        @Parcelize
        object Toggle : MobileData() {
            override val id = ActionId.TOGGLE_MOBILE_DATA
        }
    }

    @Serializable
    @Parcelize
    sealed class Brightness : ActionData() {
        @Serializable
        @Parcelize
        object EnableAuto : Brightness() {
            override val id = ActionId.ENABLE_AUTO_BRIGHTNESS
        }

        @Serializable
        @Parcelize
        object DisableAuto : Brightness() {
            override val id = ActionId.DISABLE_AUTO_BRIGHTNESS
        }

        @Serializable
        @Parcelize
        object ToggleAuto : Brightness() {
            override val id = ActionId.TOGGLE_AUTO_BRIGHTNESS
        }

        @Serializable
        @Parcelize
        object Increase : Brightness() {
            override val id = ActionId.INCREASE_BRIGHTNESS
        }

        @Serializable
        @Parcelize
        object Decrease : Brightness() {
            override val id = ActionId.DECREASE_BRIGHTNESS
        }
    }

    @Serializable
    @Parcelize
    sealed class StatusBar : ActionData() {
        @Serializable
        @Parcelize
        object ExpandNotifications : StatusBar() {
            override val id = ActionId.EXPAND_NOTIFICATION_DRAWER
        }

        @Serializable
        @Parcelize
        object ToggleNotifications : StatusBar() {
            override val id = ActionId.TOGGLE_NOTIFICATION_DRAWER
        }

        @Serializable
        @Parcelize
        object ExpandQuickSettings : StatusBar() {
            override val id = ActionId.EXPAND_QUICK_SETTINGS
        }

        @Serializable
        @Parcelize
        object ToggleQuickSettings : StatusBar() {
            override val id = ActionId.TOGGLE_QUICK_SETTINGS
        }

        @Serializable
        @Parcelize
        object Collapse : StatusBar() {
            override val id = ActionId.COLLAPSE_STATUS_BAR
        }
    }

    @Serializable
    @Parcelize
    object GoBack : ActionData() {
        override val id = ActionId.GO_BACK
    }

    @Serializable
    @Parcelize
    object GoHome : ActionData() {
        override val id = ActionId.GO_HOME
    }

    @Serializable
    @Parcelize
    object OpenRecents : ActionData() {
        override val id = ActionId.OPEN_RECENTS
    }

    @Serializable
    @Parcelize
    object GoLastApp : ActionData() {
        override val id = ActionId.GO_LAST_APP
    }

    @Serializable
    @Parcelize
    object OpenMenu : ActionData() {
        override val id = ActionId.OPEN_MENU
    }

    @Serializable
    @Parcelize
    object ToggleSplitScreen : ActionData() {
        override val id = ActionId.TOGGLE_SPLIT_SCREEN
    }

    @Serializable
    @Parcelize
    object Screenshot : ActionData() {
        override val id = ActionId.SCREENSHOT
    }

    @Serializable
    @Parcelize
    object MoveCursorToEnd : ActionData() {
        override val id = ActionId.MOVE_CURSOR_TO_END
    }

    @Serializable
    @Parcelize
    object ToggleKeyboard : ActionData() {
        override val id = ActionId.TOGGLE_KEYBOARD
    }

    @Serializable
    @Parcelize
    object ShowKeyboard : ActionData() {
        override val id = ActionId.SHOW_KEYBOARD
    }

    @Serializable
    @Parcelize
    object HideKeyboard : ActionData() {
        override val id = ActionId.HIDE_KEYBOARD
    }

    @Serializable
    @Parcelize
    object ShowKeyboardPicker : ActionData() {
        override val id = ActionId.SHOW_KEYBOARD_PICKER
    }

    @Serializable
    @Parcelize
    object CopyText : ActionData() {
        override val id = ActionId.TEXT_COPY
    }

    @Serializable
    @Parcelize
    object PasteText : ActionData() {
        override val id = ActionId.TEXT_PASTE
    }

    @Serializable
    @Parcelize
    object CutText : ActionData() {
        override val id = ActionId.TEXT_CUT
    }

    @Serializable
    @Parcelize
    object SelectWordAtCursor : ActionData() {
        override val id = ActionId.SELECT_WORD_AT_CURSOR
    }

    @Serializable
    @Parcelize
    object VoiceAssistant : ActionData() {
        override val id = ActionId.OPEN_VOICE_ASSISTANT
    }

    @Serializable
    @Parcelize
    object DeviceAssistant : ActionData() {
        override val id = ActionId.OPEN_DEVICE_ASSISTANT
    }

    @Serializable
    @Parcelize
    object OpenCamera : ActionData() {
        override val id = ActionId.OPEN_CAMERA
    }

    @Serializable
    @Parcelize
    object LockDevice : ActionData() {
        override val id = ActionId.LOCK_DEVICE
    }

    @Serializable
    @Parcelize
    object ScreenOnOff : ActionData() {
        override val id = ActionId.POWER_ON_OFF_DEVICE
    }

    @Serializable
    @Parcelize
    object SecureLock : ActionData() {
        override val id = ActionId.SECURE_LOCK_DEVICE
    }

    @Serializable
    @Parcelize
    object ConsumeKeyEvent : ActionData() {
        override val id = ActionId.CONSUME_KEY_EVENT
    }

    @Serializable
    @Parcelize
    object OpenSettings : ActionData() {
        override val id = ActionId.OPEN_SETTINGS
    }

    @Serializable
    @Parcelize
    object ShowPowerMenu : ActionData() {
        override val id = ActionId.SHOW_POWER_MENU
    }

    @Serializable
    @Parcelize
    object DismissLastNotification : ActionData() {
        override val id: ActionId = ActionId.DISMISS_MOST_RECENT_NOTIFICATION
    }

    @Serializable
    @Parcelize
    object DismissAllNotifications : ActionData() {
        override val id: ActionId = ActionId.DISMISS_ALL_NOTIFICATIONS
    }

    @Serializable
    @Parcelize
    object AnswerCall : ActionData() {
        override val id: ActionId = ActionId.ANSWER_PHONE_CALL
    }

    @Serializable
    @Parcelize
    object EndCall : ActionData() {
        override val id: ActionId = ActionId.END_PHONE_CALL
    }
}