package io.github.sds100.keymapper.actions

import android.os.Build
import io.github.sds100.keymapper.actions.sound.SoundsManager
import io.github.sds100.keymapper.shizuku.ShizukuAdapter
import io.github.sds100.keymapper.system.apps.PackageManagerAdapter
import io.github.sds100.keymapper.system.camera.CameraAdapter
import io.github.sds100.keymapper.system.camera.CameraLens
import io.github.sds100.keymapper.system.inputmethod.InputMethodAdapter
import io.github.sds100.keymapper.system.inputmethod.KeyMapperImeHelper
import io.github.sds100.keymapper.system.permissions.Permission
import io.github.sds100.keymapper.system.permissions.PermissionAdapter
import io.github.sds100.keymapper.system.permissions.SystemFeatureAdapter
import io.github.sds100.keymapper.util.Error
import io.github.sds100.keymapper.util.onFailure
import io.github.sds100.keymapper.util.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

/**
 * Created by sds100 on 15/02/2021.
 */

class GetActionErrorUseCaseImpl(
    private val packageManager: PackageManagerAdapter,
    private val inputMethodAdapter: InputMethodAdapter,
    private val permissionAdapter: PermissionAdapter,
    private val systemFeatureAdapter: SystemFeatureAdapter,
    private val cameraAdapter: CameraAdapter,
    private val soundsManager: SoundsManager,
    private val shizukuAdapter: ShizukuAdapter
) : GetActionErrorUseCase {

    private val isActionSupported = IsActionSupportedUseCaseImpl(systemFeatureAdapter)
    private val keyMapperImeHelper = KeyMapperImeHelper(inputMethodAdapter)

    override val invalidateActionErrors = merge(
        inputMethodAdapter.chosenIme.drop(1).map { },
        inputMethodAdapter.inputMethods.drop(1).map { }, //invalidate when the input methods change
        permissionAdapter.onPermissionsUpdate,
        soundsManager.soundFiles.drop(1).map { },
        shizukuAdapter.isStarted.drop(1).map { },
        shizukuAdapter.isInstalled.drop(1).map { }
    )

    override fun getErrors(actionList: List<ActionData>): Map<ActionData, Error?> {
        val errorMap = mutableMapOf<ActionData, Error?>()

        /*
        See issue #797. Simulate which ime is chosen while the actions are being performed. This is so that any
        actions that depend on a compatible ime being chosen only show an error if no previous action will select
        the correct ime for it.
         */
        var simulatedChosenIme: String? = null

        for (action in actionList) {
            var error: Error? = null

            if (action is ActionData.SwitchKeyboard) {
                simulatedChosenIme = action.imeId
            }

            if (action.canUseShizukuToPerform() && shizukuAdapter.isInstalled.value) {
                if (!(action.canUseImeToPerform() && keyMapperImeHelper.isCompatibleImeChosen())) {
                    when {
                        !shizukuAdapter.isStarted.value ->
                            error = Error.ShizukuNotStarted

                        !permissionAdapter.isGranted(Permission.SHIZUKU) ->
                            error = Error.PermissionDenied(Permission.SHIZUKU)
                    }
                }
            } else if (action.canUseImeToPerform()) {
                if (!keyMapperImeHelper.isCompatibleImeEnabled()) {
                    error = Error.NoCompatibleImeEnabled
                }

                val isCompatibleImeChosen =
                    if (simulatedChosenIme != null) {
                        keyMapperImeHelper.isCompatibleIme(simulatedChosenIme)
                    } else {
                        keyMapperImeHelper.isCompatibleImeChosen()
                    }

                if (!isCompatibleImeChosen) {
                    error = Error.NoCompatibleImeChosen
                }
            }

            isActionSupported.invoke(action.id)?.let {
                error = it
            }

            ActionUtils.getRequiredPermissions(action.id).forEach { permission ->
                if (!permissionAdapter.isGranted(permission)) {
                    error = Error.PermissionDenied(permission)
                }
            }

            when (action) {
                is ActionData.App -> {
                    error = getAppError(action.packageName)
                }

                is ActionData.AppShortcut -> {
                    if (action.packageName != null) {
                        error = getAppError(action.packageName)
                    }
                }

                is ActionData.InputKeyEvent ->
                    if (action.useShell && !permissionAdapter.isGranted(Permission.ROOT)) {
                        error = Error.PermissionDenied(Permission.ROOT)
                    }

                is ActionData.TapScreen ->
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        error = Error.SdkVersionTooLow(Build.VERSION_CODES.N)
                    }

                is ActionData.PhoneCall ->
                    if (!permissionAdapter.isGranted(Permission.CALL_PHONE)) {
                        error = Error.PermissionDenied(Permission.CALL_PHONE)
                    }

                is ActionData.Sound -> {
                    soundsManager.getSound(action.soundUid).onFailure { soundError ->
                        error = soundError
                    }
                }

                is ActionData.VoiceAssistant -> {
                    if (!packageManager.isVoiceAssistantInstalled()) {
                        error = Error.NoVoiceAssistant
                    }
                }

                is ActionData.Flashlight ->
                    if (!cameraAdapter.hasFlashFacing(action.lens)) {
                        error = when (action.lens) {
                            CameraLens.FRONT -> Error.FrontFlashNotFound
                            CameraLens.BACK -> Error.BackFlashNotFound
                        }
                    }

                is ActionData.SwitchKeyboard ->
                    inputMethodAdapter.getInfoById(action.imeId).onFailure { imeError ->
                        error = imeError
                    }
            }

            errorMap[action] = error
        }

        return errorMap
    }

    private fun getAppError(packageName: String): Error? {
        packageManager.isAppEnabled(packageName).onSuccess { isEnabled ->
            if (!isEnabled) {
                return Error.AppDisabled(packageName)
            }
        }

        if (!packageManager.isAppInstalled(packageName)) {
            return Error.AppNotFound(packageName)
        }

        return null
    }
}

interface GetActionErrorUseCase {
    val invalidateActionErrors: Flow<Unit>
    fun getErrors(actionList: List<ActionData>): Map<ActionData, Error?>
}