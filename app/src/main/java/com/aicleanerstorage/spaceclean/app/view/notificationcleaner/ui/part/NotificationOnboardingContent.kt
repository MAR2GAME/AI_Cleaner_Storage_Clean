package com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import kotlinx.coroutines.delay

@Composable
internal fun NotificationOnboardingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NotificationCleanerGuideCarousel(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(400.dp),
        )
        Spacer(modifier = Modifier.height(70.dp))
        Text(
            text = stringResource(R.string.notification_tidy_one_tap),
            color = Navy,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

internal val Navy = Color(0xFF2D3748)

private val Divider15 = Color(0xFFE2EAF3)

private val NotificationStatusRowHeight = 84.dp

private const val NOTIFICATION_GUIDE_SLIDE_DELAY_MILLIS = 2_000L

private val NotificationGuideSlides =
    listOf(
        R.drawable.notification_bar_guide_1,
        R.drawable.notification_bar_guide_2,
        R.drawable.notification_bar_guide_3,
        R.drawable.notification_bar_guide_4,
    )

@Composable
private fun NotificationCleanerGuideCarousel(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { NotificationGuideSlides.size })

    LaunchedEffect(pagerState) {
        while (true) {
            val nextPage = (pagerState.currentPage + 1) % NotificationGuideSlides.size
            pagerState.animateScrollToPage(nextPage)
            delay(NOTIFICATION_GUIDE_SLIDE_DELAY_MILLIS)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.clipToBounds(),
        verticalAlignment = Alignment.CenterVertically,
    ) { page ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(NotificationGuideSlides[page]),
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
    }
}
