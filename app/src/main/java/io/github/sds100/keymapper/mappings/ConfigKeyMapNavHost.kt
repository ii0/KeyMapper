package io.github.sds100.keymapper.mappings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
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
import io.github.sds100.keymapper.system.apps.ChooseActivityScreen
import io.github.sds100.keymapper.system.apps.ChooseAppScreen
import io.github.sds100.keymapper.system.apps.ChooseAppShortcutScreen
import io.github.sds100.keymapper.system.intents.ConfigIntentScreen

@Composable
fun ConfigKeyMapNavHost(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    keyMapUid: String?,
) {
    DestinationsNavHost(modifier = modifier, navGraph = NavGraphs.root) {
        composable(ConfigKeyMapScreenDestination) {
            val triggerViewModel: ConfigKeyMapViewModel2 = hiltViewModel()

            if (keyMapUid == null) {
                triggerViewModel.loadNewKeyMap()
            } else {
                triggerViewModel.loadKeyMap(keyMapUid)
            }

            ConfigKeyMapScreen(
                viewModel = triggerViewModel,
                navigateBack = navigateBack
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