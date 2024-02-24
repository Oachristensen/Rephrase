package com.owen.rephrase.handlers

import android.graphics.Path
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.os.Handler
import com.owen.rephrase.MainActivity
import com.owen.rephrase.R
import java.net.URI

class MediaHandler {
    lateinit var mediaPlayer: MediaPlayer
    private var mediaDuration: Int = 0
    private var curMediaTimeMillis: Int = 0
    private val handler : Handler = Handler()
    private lateinit var mainActivity : MainActivity
    private var mediaDurationMax = ""




fun prepareMediaPlayer(i: MainActivity) {
    mainActivity = i
    mediaPlayer = MediaPlayer()
    mediaPlayer.setOnPreparedListener {
        mediaDuration = mediaPlayer.duration
        handler.post(updateRunnable)
    }
}


    companion object {
        fun millisecondsToSecondsFormatted(millis: Int): String {
            val seconds: Int = millis / 1000
            val minutes: Int = seconds / 60
            val remSeconds: Int = (seconds % 60)
            var result = "$minutes:$remSeconds"
            if (remSeconds.toString().length == 1) {
                result = "$minutes:0$remSeconds"
            }
            return (result)
        }
    }

    fun mediaSetSeek(float: Float) {
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.seekTo((mediaDuration * float).toInt())
        }
        else{
            println("MediaPlayerNotInitalized")
        }
    }


    fun mediaPlayPause() {
        if (!this::mediaPlayer.isInitialized) {
            println("MediaPlayerNotInitalized")
        }
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            return
        }

        mediaPlayer.start()
    }


    private val updateRunnable = object : Runnable {
        override fun run() {
            curMediaTimeMillis = mediaPlayer.currentPosition
            mediaDurationMax = millisecondsToSecondsFormatted(mediaPlayer.duration)
            mainActivity.setTimerTextViewText(millisecondsToSecondsFormatted(curMediaTimeMillis) + "/" + mediaDurationMax)

            handler.postDelayed(this, 1000)
        }
    }
    fun setMediaDataSource(source : Uri) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(mainActivity.baseContext, source)
        mediaPlayer.prepareAsync()
    }

}
