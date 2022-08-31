package io.github.sds100.keymapper.mappings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.bottomSheetComposable
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.scope.resultBackNavigator
import com.ramcosta.composedestinations.scope.resultRecipient
import io.github.sds100.keymapper.NavGraphs
import io.github.sds100.keymapper.actions.ChooseActionScreen
import io.github.sds100.keymapper.actions.keyevent.ChooseKeyCodeScreen
import io.github.sds100.keymapper.actions.keyevent.ConfigKeyEventScreen
import io.github.sds100.keymapper.actions.sound.ChooseSoundScreen
import io.github.sds100.keymapper.actions.tapscreen.CreateTapScreenActionScreen
import io.github.sds100.keymapper.destinations.*
import io.github.sds100.keymapper.mappings.keymaps.ConfigKeyMapScreen
import io.github.sds100.keymapper.mappings.keymaps.trigger.ConfigKeyMapViewModel2
import io.github.sds100.keymapper.mappings.keymaps.trigger.ConfigTriggerKeyScreen
import io.github.sds100.keymapper.system.apps.ChooseActivityScreen
import io.github.sds100.keymapper.system.apps.ChooseAppScreen
import io.github.sds100.keymapper.system.apps.ChooseAppShortcutScreen
import io.github.sds100.keymapper.system.intents.ConfigIntentScreen
import io.github.sds100.keymapper.theme.Shapes

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
fun ConfigKeyMapNavHost(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    keyMapUid: String?,
) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController()
    navController.navigatorProvider += bottomSheetNavigator
    val configKeyMapViewModel: ConfigKeyMapViewModel2 = hiltViewModel()

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetShape = Shapes.bottomSheet()
    ) {
        DestinationsNavHost(
            modifier = modifier,
            navController = navController,
            navGraph = NavGraphs.root,
            engine = rememberAnimatedNavHostEngine()
        ) {
            composable(ConfigKeyMapScreenDestination) {
                if (keyMapUid == null) {
                    configKeyMapViewModel.loadNewKeyMap()
                } else {
                    configKeyMapViewModel.loadKeyMap(keyMapUid)
                }

                ConfigKeyMapScreen(
                    viewModel = configKeyMapViewModel,
                    navigateBack = navigateBack,
                    navigator = destinationsNavigator,
                    chooseActionResultRecipient = resultRecipient()
                )
            }

            bottomSheetComposable(ConfigTriggerKeyScreenDestination) {
                ConfigTriggerKeyScreen(
                    viewModel = configKeyMapViewModel,
                    navigator = destinationsNavigator
                )
            }

            composable(ChooseActionScreenDestination) {
                ChooseActionScreen(
                    viewModel = hiltViewModel(),
                    navigator = destinationsNavigator,
                    resultBackNavigator = resultBackNavigator(),
                    keyCodeResultRecipient = resultRecipient(),
                    appResultRecipient = resultRecipient(),
                    appShortcutResultRecipient = resultRecipient(),
                    tapScreenActionResultRecipient = resultRecipient(),
                    chooseSoundResultRecipient = resultRecipient(),
                    configKeyEventResultRecipient = resultRecipient(),
                    configIntentResultRecipient = resultRecipient()
                )
            }

            composable(ChooseKeyCodeScreenDestination) {
                ChooseKeyCodeScreen(
                    viewModel = hiltViewModel(),
                    resultNavigator = resultBackNavigator()
                )
            }

            composable(ChooseAppScreenDestination) {
                ChooseAppScreen(
                    viewModel = hiltViewModel(),
                    resultBackNavigator = resultBackNavigator()
                )
            }

            composable(ChooseAppShortcutScreenDestination) {
                ChooseAppShortcutScreen(
                    viewModel = hiltViewModel(),
                    resultBackNavigator = resultBackNavigator()
                )
            }

            composable(CreateTapScreenActionScreenDestination) {
                CreateTapScreenActionScreen(
                    viewModel = hiltViewModel(),
                    resultBackNavigator = resultBackNavigator()
                )
            }

            composable(ChooseSoundScreenDestination) {
                ChooseSoundScreen(
                    viewModel = hiltViewModel(),
                    resultBackNavigator = resultBackNavigator()
                )
            }

            composable(ConfigKeyEventScreenDestination) {
                ConfigKeyEventScreen(
                    viewModel = hiltViewModel(),
                    resultBackNavigator = resultBackNavigator(),
                    navigator = destinationsNavigator,
                    keyCodeResultRecipient = resultRecipient()
                )
            }

            composable(ConfigIntentScreenDestination) {
                ConfigIntentScreen(
                    viewModel = hiltViewModel(),
                    resultBackNavigator = resultBackNavigator(),
                    navigator = destinationsNavigator,
                    activityResultRecipient = resultRecipient()
                )
            }

            composable(ChooseActivityScreenDestination) {
                ChooseActivityScreen(
                    viewModel = hiltViewModel(),
                    resultBackNavigator = resultBackNavigator()
                )
            }
        }
    }
}