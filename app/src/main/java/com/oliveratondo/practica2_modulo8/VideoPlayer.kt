package com.oliveratondo.practica2_modulo8

import android.app.ActionBar.LayoutParams
import android.media.MediaPlayer
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    url: String,
    audioUrl: String,
    modifier: Modifier = Modifier,
    onLoadingChange: (Boolean) -> Unit
) {
    val context = LocalContext.current

    val exoPlayer = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    val mediaPlayer = remember(audioUrl) {
        try {
            MediaPlayer().apply {
                setDataSource(audioUrl)
                isLooping = false
                prepareAsync()
            }
        } catch (e: Exception) {
            null
        }
    }

    DisposableEffect(
        AndroidView(factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }
        }, modifier = modifier)
    ) {
        var videoReady = false
        var audioReady = false

        fun tryStartPlayback() {
            if (videoReady && audioReady) {
                exoPlayer.playWhenReady = true
                mediaPlayer?.start()
                onLoadingChange(false)
            }
        }

        val videoListener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    videoReady = true
                    tryStartPlayback()
                }
            }
        }

        val audioListener = MediaPlayer.OnPreparedListener {
            audioReady = true
            tryStartPlayback()
        }

        exoPlayer.addListener(videoListener)
        mediaPlayer?.setOnPreparedListener(audioListener)

        onDispose {
            exoPlayer.removeListener(videoListener)
            exoPlayer.release()

            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        }
    }
}
