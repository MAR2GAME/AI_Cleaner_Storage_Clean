package com.aicleanerstorage.spaceclean.app.view.common.tool

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.base.designsystem.component.stableNavigationBarsPadding
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

/**
 * 通用结果页内容：完成动画 + 工具推荐。
 *
 * 这是业务组合组件，不属于纯设计系统；放在 app/view/common/tool。
 *
 * @param onNavigateTool 点击工具推荐时的导航回调。
 * @param excludedToolDestinations 不展示的工具目的地。
 * @param completionContent 结果页自己的完成动画/文案。
 */
@Composable
fun CommonResultContent(
    onNavigateTool: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
    excludedToolDestinations: Set<AppDestination> = emptySet(),
    completionContent: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .stableNavigationBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            completionContent()
            Spacer(modifier = Modifier.height(24.dp))
            ToolFeatureBanners(
                onNavigateTool = onNavigateTool,
                excludedDestinations = excludedToolDestinations,
            )
        }
    }
}
