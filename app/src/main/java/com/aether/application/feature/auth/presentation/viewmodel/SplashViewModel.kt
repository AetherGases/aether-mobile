package com.aether.application.feature.auth.presentation.viewmodel

import android.content.Context
import androidx.annotation.RawRes
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.aether.application.core.auth.storage.SessionManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SplashViewModel(
    private val sessionManager: SessionManager,
    context: Context,
    @RawRes videoRes: Int,
    startDelayMillis: Long = 300,
    whiteStartFraction: Float = 0.45f,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()
    private val _events = Channel<SplashEvent>(Channel.BUFFERED)
    val events: Flow<SplashEvent> = _events.receiveAsFlow()

    val circleDurationMillis = 800
    val player: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/$videoRes".toUri()))
        prepare()
        playWhenReady = false
    }

    private var sessionRestored = false
    private var videoFinished = false
    private var hasNavigated = false

    init {
        viewModelScope.launch {
            delay(startDelayMillis.milliseconds)
            _uiState.update { it.copy(greenClosing = true) }
            delay((circleDurationMillis * whiteStartFraction).toLong().milliseconds)
            _uiState.update { it.copy(whiteClosing = true) }
        }
        // listener para o vídeo -> se terminar, tenta mudar de página
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        viewModelScope.launch {
                            videoFinished = true
                            navigate()
                        }
                    }
            }
        })
        // autenticação termina -> tenta mudar de página
        viewModelScope.launch {
            sessionManager.restoreSession()
            sessionRestored = true
            navigate()
        }
    }

    fun onIrisOpened() {
        _uiState.update { it.copy(showLogo = true) }
        player.play()
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }

    private suspend fun navigate() {
        if (!videoFinished || !sessionRestored || hasNavigated) return
        hasNavigated = true
        val event = if (sessionManager.isAuthenticated()) SplashEvent.NavigateToHome else SplashEvent.NavigateToAuth
        _events.send(event)
    }
}

data class SplashUiState(
    val greenClosing: Boolean = false,
    val whiteClosing: Boolean = false,
    val showLogo: Boolean = false,
)

sealed interface SplashEvent {
    data object NavigateToAuth : SplashEvent
    data object NavigateToHome : SplashEvent
}