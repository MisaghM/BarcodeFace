package com.misana.barcodeface.presentation.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.misana.barcodeface.ui.theme.MainGradient
import com.misana.barcodeface.presentation.settings.SettingsViewModel
import kotlinx.coroutines.launch

const val fadeDuration: Int = 150

@Composable
fun WelcomeScreen(
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState { WelcomeContent.pages.size }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.weight(10f)
            ) { page ->
                PagerScreen(page = WelcomeContent.pages[page])
            }
            PageIndicator(pagerState = pagerState)
            FinishButton(
                text = "Finish",
                pagerState = pagerState,
                onClick = onFinish,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PagerScreen(
    page: WelcomeContent,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = page.image),
            contentDescription = "BarcodeFace Logo",
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .fillMaxHeight(0.5f)
        )
        Text(
            text = page.title,
            style = MaterialTheme.typography.displayMedium.merge(
                TextStyle(brush = Brush.linearGradient(MainGradient))
            ),
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 42.dp)
        )
    }
}

@Composable
fun PageIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        repeat(pagerState.pageCount) {
            val color: Color by animateColorAsState(
                targetValue = if (it == pagerState.currentPage)
                    MaterialTheme.colorScheme.onSurface else
                    MaterialTheme.colorScheme.outlineVariant,
                animationSpec = tween(durationMillis = fadeDuration),
                label = "Page indicator color animation"
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(16.dp)
                    .clickable {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(it)
                        }
                    }
            )
        }
    }
}


@Composable
fun FinishButton(
    text: String,
    pagerState: PagerState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.padding(horizontal = 40.dp)
    ) {
        AnimatedVisibility(
            visible = pagerState.currentPage == pagerState.pageCount - 1,
            enter = fadeIn(animationSpec = tween(durationMillis = fadeDuration)),
            exit = fadeOut(animationSpec = tween(durationMillis = fadeDuration)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onClick) {
                Text(text)
            }
        }
    }
}
