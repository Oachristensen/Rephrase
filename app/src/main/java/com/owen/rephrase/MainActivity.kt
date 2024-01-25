package com.owen.rephrase

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var mainButton: Button
    private lateinit var mainTextView1: TextView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var intent: Intent
    private var context: Context = this
    private lateinit var nowPlayingTextView: TextView
    private lateinit var audioSeekBar: SeekBar
    private var mediaDuration: Int = 0
    private lateinit var timerTextView: TextView
    private val handler : Handler = Handler()
    private var curMediaTimeMillis: Int = 0
    private var audioFile: String = "hazard_duty_pay"
    private lateinit var mediaDurationMax : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainButton = findViewById(R.id.mainButton)
        mainTextView1 = findViewById(R.id.mainTextView1)
        nowPlayingTextView = findViewById(R.id.nowPlayingTextView)
        audioSeekBar = findViewById(R.id.audioSeekBar)
        timerTextView = findViewById(R.id.timerTextView)

        // TODO: MOVE THIS TO THE SELECTION OF AN MP3 FILE
        mediaPlayer = MediaPlayer()
//        val rawResourceId = R.raw.hazard_duty_pay
//        val rawFileDescriptor = resources.openRawResourceFd(rawResourceId)
//        mediaPlayer.setDataSource(rawFileDescriptor.fileDescriptor, rawFileDescriptor.startOffset, rawFileDescriptor.length)
//        mediaPlayer.prepareAsync()



//        mediaPlayer.setOnPreparedListener {
//            mediaDuration = mediaPlayer.duration
//            println(mediaDuration)
//            handler.post(updateRunnable)
//        }



        audioSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "MainActivity $progress")
                val progressPercent: Float = (progress.toFloat() / 100)
                mediaPlayer.seekTo((mediaDuration*progressPercent).toInt())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    fun speak(view: View) {
        intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now")
        startActivityForResult(intent, 100)
    }

    fun mediaControl(view: View) {
        if (!this::mediaPlayer.isInitialized) {
        }
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            return
        }
        mediaPlayer.start()
    }

    fun selectMP3File(view: View) {
        // code for selecting file
//        nowPlayingTextView.text = FILENAME
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null) {
                val pastText: String = mainTextView1.text.toString()
                mainTextView1.text =
                    (pastText + "\n" + data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!!.get(0))
            }
        }

    }

    fun millisecondsToSecondsFormatted(millis: Int): String {
        val seconds: Int = millis / 1000
        val minutes: Int = seconds / 60
        val remSeconds: Int = (seconds % 60)
        return ("$minutes:$remSeconds")
    }
    private val updateRunnable = object : Runnable {
        override fun run() {
            // Update your variable based on the current playback position
            curMediaTimeMillis = mediaPlayer.currentPosition
            val mediaDurationMax = millisecondsToSecondsFormatted(mediaPlayer.duration)
            timerTextView.text = (millisecondsToSecondsFormatted(curMediaTimeMillis) + "/" + mediaDurationMax)

            // Schedule the next update after a delay (e.g., every 1000 milliseconds)
            handler.postDelayed(this, 1000)
        }
    }

}

