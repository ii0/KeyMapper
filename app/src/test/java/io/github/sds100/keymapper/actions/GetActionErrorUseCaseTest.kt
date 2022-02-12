package io.github.sds100.keymapper.actions

import android.view.KeyEvent
import io.github.sds100.keymapper.shizuku.ShizukuAdapter
import io.github.sds100.keymapper.system.inputmethod.ImeInfo
import io.github.sds100.keymapper.system.inputmethod.InputMethodAdapter
import io.github.sds100.keymapper.system.permissions.Permission
import io.github.sds100.keymapper.system.permissions.PermissionAdapter
import io.github.sds100.keymapper.util.Error
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Created by sds100 on 01/05/2021.
 */

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class GetActionErrorUseCaseTest {

    private val testDispatcher = TestCoroutineDispatcher()
    private val coroutineScope = TestCoroutineScope(testDispatcher)

    private lateinit var useCase: GetActionErrorUseCaseImpl

    private lateinit var mockShizukuAdapter: ShizukuAdapter
    private lateinit var mockInputMethodAdapter: InputMethodAdapter
    private lateinit var mockPermissionAdapter: PermissionAdapter

    @Before
    fun init() {
        mockShizukuAdapter = mock()
        mockInputMethodAdapter = mock()
        mockPermissionAdapter = mock()

        useCase = GetActionErrorUseCaseImpl(
            packageManager = mock(),
            inputMethodAdapter = mockInputMethodAdapter,
            permissionAdapter = mockPermissionAdapter,
            systemFeatureAdapter = mock(),
            cameraAdapter = mock(),
            soundsManager = mock(),
            shizukuAdapter = mockShizukuAdapter
        )
    }

    /**
     * #797
     */
    @Test
    fun `return error that ime must be chosen if there are no previous actions change the ime`() =
        coroutineScope.runBlockingTest {
            //GIVEN
            val actionList = mutableListOf(
                ActionData.InputKeyEvent(KeyEvent.KEYCODE_A)
            )

            //WHEN
            whenever(mockShizukuAdapter.isInstalled).then { MutableStateFlow(false) }

            val fakeImeInfo = ImeInfo(
                id = "bla",
                packageName = "not_key_mapper",
                label = "not key mapper",
                isEnabled = true,
                isChosen = true
            )

            whenever(mockInputMethodAdapter.inputMethods).then { MutableStateFlow(listOf(fakeImeInfo)) }
            whenever(mockInputMethodAdapter.chosenIme).then { MutableStateFlow(fakeImeInfo) }

            val errorMap = useCase.getErrors(actionList)

            //THEN
            assertThat(errorMap[actionList[0]], `is`(Error.NoCompatibleImeChosen))
        }

    /**
     * #797
     */
    @Test
    fun `don't return error that ime must be chosen if previous action selects that ime`() =
        coroutineScope.runBlockingTest {
            //GIVEN
            val keyMapperKeyboardImeId = "io.github.sds100.keymapper.inputmethod.latin/.LatinIME"
            val actionList = mutableListOf(
                ActionData.SwitchKeyboard(keyMapperKeyboardImeId, "Key Mapper GUI Keyboard"),
                ActionData.InputKeyEvent(KeyEvent.KEYCODE_A)
            )

            //WHEN
            whenever(mockShizukuAdapter.isInstalled).then { MutableStateFlow(false) }

            val fakeImeInfo = ImeInfo(
                id = keyMapperKeyboardImeId,
                packageName = "io.github.sds100.keymapper.inputmethod.latin",
                label = "Key Mapper GUI Keyboard",
                isEnabled = true,
                isChosen = true
            )

            whenever(mockInputMethodAdapter.inputMethods).then { MutableStateFlow(listOf(fakeImeInfo)) }
            whenever(mockPermissionAdapter.isGranted(Permission.WRITE_SECURE_SETTINGS)).then { true }

            val errorMap = useCase.getErrors(actionList)

            //THEN
            assertThat(errorMap[actionList[0]], nullValue())
            assertThat(errorMap[actionList[1]], nullValue())
        }

    /**
     * #776
     */
    @Test
    fun `dont show Shizuku errors if a compatible ime is selected`() = coroutineScope.runBlockingTest {
        //GIVEN
        whenever(mockShizukuAdapter.isInstalled).then { MutableStateFlow(true) }
        whenever(mockInputMethodAdapter.chosenIme).then {
            MutableStateFlow(
                ImeInfo(
                    id = "ime_id",
                    packageName = "io.github.sds100.keymapper.inputmethod.latin",
                    label = "Key Mapper GUI Keyboard",
                    isEnabled = true,
                    isChosen = true
                )
            )
        }

        val action = ActionData.InputKeyEvent(keyCode = KeyEvent.KEYCODE_VOLUME_DOWN)

        //WHEN
        val errorMap = useCase.getErrors(listOf(action))
        val error = errorMap[action]

        //THEN
        assertThat(error, nullValue())
    }

    /**
     * #776
     */
    @Test
    fun `show Shizuku errors if a compatible ime is not selected and Shizuku is installed`() = coroutineScope.runBlockingTest {
        //GIVEN
        whenever(mockShizukuAdapter.isInstalled).then { MutableStateFlow(true) }
        whenever(mockShizukuAdapter.isStarted).then { MutableStateFlow(false) }

        whenever(mockInputMethodAdapter.chosenIme).then {
            MutableStateFlow(
                ImeInfo(
                    id = "ime_id",
                    packageName = "io.gboard",
                    label = "Gboard",
                    isEnabled = true,
                    isChosen = true
                )
            )
        }

        val action = ActionData.InputKeyEvent(keyCode = KeyEvent.KEYCODE_VOLUME_DOWN)

        //WHEN
        val errorMap = useCase.getErrors(listOf(action))
        val error = errorMap[action]

        //THEN
        assertThat(error, `is`(Error.ShizukuNotStarted))
    }
}