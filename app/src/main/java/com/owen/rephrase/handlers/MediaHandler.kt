package com.owen.rephrase.handlers

import android.media.MediaPlayer
import android.os.Handler
import com.owen.rephrase.MainActivity
import com.owen.rephrase.R

class MediaHandler {
    lateinit var mediaPlayer: MediaPlayer
    private var mediaDuration: Int = 0
    private var curMediaTimeMillis: Int = 0
    private val handler : Handler = Handler()
    private lateinit var mainActivity : MainActivity





fun prepareMediaPlayer(i: MainActivity) {
    mainActivity = i
    mediaPlayer = MediaPlayer()
    val rawResourceId = R.raw.hazard_duty_pay
    val rawFileDescriptor = mainActivity.resources.openRawResourceFd(rawResourceId)
    mediaPlayer.setDataSource(rawFileDescriptor.fileDescriptor, rawFileDescriptor.startOffset, rawFileDescriptor.length)
    mediaPlayer.prepareAsync()



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
            return ("$minutes:$remSeconds")
        }
    }

    fun mediaSetSeek(float: Float) {
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.seekTo((mediaDuration * float).toInt())
        }
    }


    fun mediaPlayPause() {
        if (!this::mediaPlayer.isInitialized) {
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
            val mediaDurationMax = millisecondsToSecondsFormatted(mediaPlayer.duration)
            mainActivity.setTimerTextViewText(millisecondsToSecondsFormatted(curMediaTimeMillis) + "/" + mediaDurationMax)

            handler.postDelayed(this, 1000)
        }
    }

}
