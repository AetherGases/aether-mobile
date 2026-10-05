package com.aether.application.feature.onboarding.presentation.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.core.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPageData(
    @DrawableRes val illustrationRes: Int,
    val title: String,
    val description: String
)

@Composable
fun OnboardingScreen(
    pages: List<OnboardingPageData>,
    onSkipClick: () -> Unit,
    onFinishClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val page = pages[pageIndex]

            OnboardingPageContent(
                page = page,
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
                pageIndex = pageIndex,
                isFirstPage = pageIndex == 0,
                isLastPage = pageIndex == pages.lastIndex,
                onBackOrSkipClick = {
                    if (pageIndex == 0) {
                        onSkipClick()
                    } else {
                        coroutineScope.launch { pagerState.animateScrollToPage(pageIndex - 1) }
                    }
                },
                onNextOrFinishClick = {
                    if (pageIndex == pages.lastIndex) {
                        onFinishClick()
                    } else {
                        coroutineScope.launch { pagerState.animateScrollToPage(pageIndex + 1) }
                    }
                }
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPageData,
    pageCount: Int,
    currentPage: Int,
    pageIndex: Int,
    isFirstPage: Boolean,
    isLastPage: Boolean,
    onBackOrSkipClick: () -> Unit,
    onNextOrFinishClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_top_logo),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .padding(top = 16.dp)
                .height(48.dp)
                .size(600.dp)
        )

        Spacer(Modifier.weight(1f))

        val baseColor = if (pageIndex == 1) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.secondary
        }

        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .background(baseColor.copy(alpha = 0.06f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(baseColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = page.illustrationRes),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(300.dp)
                )
            }
        }

        Spacer(Modifier.height(48.dp))

        Text(
            text = page.title,
            style = titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = page.description,
            style = bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(pageCount) { dotIndex ->
                PageDot(isActive = dotIndex == currentPage)
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isFirstPage) "Pular" else "Voltar",
                style = bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier
                    .clickable(onClick = onBackOrSkipClick)
                    .padding(8.dp)
            )

            if (isLastPage) {
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(100),
                            spotColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                        )
                        .clip(RoundedCornerShape(100))
                        .background(MaterialTheme.colorScheme.secondary)
                        .clickable(onClick = onNextOrFinishClick)
                        .padding(horizontal = 32.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Começar",
                        style = titleMediumMD.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            spotColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                        )
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                        .clickable(onClick = onNextOrFinishClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Próximo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PageDot(isActive: Boolean) {
    Box(
        modifier = Modifier
            .height(6.dp)
            .width(if (isActive) 20.dp else 6.dp)
            .clip(RoundedCornerShape(50))
            .background(
                if (isActive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                }
            )
    )
}

private val previewOnboardingPages = listOf(
    OnboardingPageData(
        illustrationRes = R.drawable.ic_onboarding_diagnostico,
        title = "Diagnóstico Preciso",
        description = "Faça o upload dos seus relatórios e descubra as melhores alternativas de gases verdes para a sua operação."
    ),
    OnboardingPageData(
        illustrationRes = R.drawable.ic_onboarding_roi,
        title = "Calcule seu ROI",
        description = "Simule o tempo de retorno e visualize com clareza o ganho financeiro que a transição energética trará para a sua empresa."
    ),
    OnboardingPageData(
        illustrationRes = R.drawable.ic_onboarding_certificacoes,
        title = "Certificações",
        description = "Acompanhe a redução das suas emissões e prepare sua marca para conquistar selos ambientais reconhecidos no mercado."
    )
)

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun OnboardingScreenFirstPagePreview() {
    AetherTheme {
        OnboardingScreen(
            pages = previewOnboardingPages,
            onSkipClick = {},
            onFinishClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun OnboardingScreenLastPagePreview() {
    AetherTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            OnboardingPageContent(
                page = previewOnboardingPages.last(),
                pageCount = previewOnboardingPages.size,
                currentPage = previewOnboardingPages.lastIndex,
                pageIndex = previewOnboardingPages.lastIndex,
                isFirstPage = false,
                isLastPage = true,
                onBackOrSkipClick = {},
                onNextOrFinishClick = {},
                modifier = Modifier
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun OnboardingScreenSecondPagePreview() {
    AetherTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            OnboardingPageContent(
                page = previewOnboardingPages[1],
                pageCount = previewOnboardingPages.size,
                currentPage = 1,
                pageIndex = 1,
                isFirstPage = false,
                isLastPage = false,
                onBackOrSkipClick = {},
                onNextOrFinishClick = {},
                modifier = Modifier
            )
        }
    }
}

