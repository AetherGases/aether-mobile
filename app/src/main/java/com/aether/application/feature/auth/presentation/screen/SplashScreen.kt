package com.aether.application.feature.auth.presentation.screen

import androidx.annotation.OptIn
import androidx.compose.animation.core.EaseInQuart
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.state.rememberPresentationState
import com.aether.core.ui.theme.green500
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    logoWidthFraction: Float = 1f,
    logoMaxWidth: Dp = 1000.dp,
    logoAspectRatio: Float = 1f,
    circleDurationMillis: Int,
    onIrisOpened: () -> Unit = {},
    greenClosing: Boolean,
    whiteClosing: Boolean,
    showLogo: Boolean,
    player: Player
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        CircleCloseOverlay(
            closing = greenClosing,
            color = green500,
            durationMillis = circleDurationMillis,
        )

        CircleCloseOverlay(
            closing = whiteClosing,
            color = Color.White,
            durationMillis = circleDurationMillis,
            onClosed = onIrisOpened
        )

        if (showLogo) {
            LogoVideo(
                player = player,
                modifier = Modifier
                    .fillMaxWidth(logoWidthFraction)
                    .widthIn(max = logoMaxWidth)
                    .aspectRatio(logoAspectRatio),
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun LogoVideo(
    player: Player,
    modifier: Modifier = Modifier,
) {
    val presentationState = rememberPresentationState(player)

    Box(modifier) {
        PlayerSurface(
            player = player,
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
            modifier = Modifier.matchParentSize()
        )
        if (presentationState.coverSurface) {
            Box(Modifier.matchParentSize().background(Color.White))
        }
    }
}

@Composable
fun CircleCloseOverlay(
    closing: Boolean,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    focus: Offset? = null,
    easing: Easing = EaseInQuart,
    durationMillis: Int,
    onClosed: () -> Unit = {},
) {

    val progress by animateFloatAsState(
        targetValue = if (closing) 1f else 0f,
        animationSpec = tween(durationMillis, easing = easing),
        finishedListener = { if (it == 1f) onClosed() },
        label = "circleClose",
    )

    if (progress == 0f) return

    Canvas(
        modifier
            .fillMaxSize()
            // Offscreen layer so BlendMode.Clear only cuts through the overlay
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    ) {
        val c = focus ?: center
        val maxRadius = listOf(
            Offset(0f, 0f),
            Offset(size.width, 0f),
            Offset(0f, size.height),
            Offset(size.width, size.height),
        ).maxOf { (it - c).getDistance() }

        drawRect(color)
        drawCircle(
            color = Color.Transparent,
            radius = maxRadius * (1f - progress),
            center = c,
            blendMode = BlendMode.Clear,
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun SplashIrisScreenPreview() {
    var greenClosing by remember { mutableStateOf(false) }
    var whiteClosing by remember { mutableStateOf(false) }
    var showLogo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300.milliseconds)
        greenClosing = true
        delay(360.milliseconds)
        whiteClosing = true
    }

    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }
    DisposableEffect(Unit) { onDispose { player.release() } }

    SplashScreen(
        circleDurationMillis = 800,
        greenClosing = greenClosing,
        whiteClosing = whiteClosing,
        showLogo = showLogo,
        onIrisOpened = { showLogo = true },
        player = player,
    )
}