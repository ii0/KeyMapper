package io.github.sds100.keymapper.mappings.keymaps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import io.github.sds100.keymapper.mappings.keymaps.trigger.ConfigTriggerScreen
import io.github.sds100.keymapper.mappings.keymaps.trigger.ConfigTriggerViewModel
import io.github.sds100.keymapper.util.ui.pagerTabIndicatorOffset
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun ConfigKeyMapScreen(
    modifier: Modifier = Modifier,
    triggerViewModel: ConfigTriggerViewModel
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val tabTitles = remember { listOf("Trigger", "Actions") }

    Scaffold(modifier = modifier, bottomBar = {
        BottomAppBar(actions = {

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
                0 -> {
                    ConfigTriggerScreen(triggerViewModel)
                }
                1 -> {
                    ConfigTriggerScreen(triggerViewModel)
                }
            }
        }
    }
}

@Preview(device = Devices.PIXEL_4)
@Composable
private fun Preview() {
//    ConfigKeyMapScreen()
}