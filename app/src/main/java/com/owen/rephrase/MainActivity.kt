package com.owen.rephrase

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.owen.rephrase.handlers.MediaHandler


private const val TAG = "MainActivity"
private const val PICKAUDIOFILE = 1
private const val PROMPTSPEECH = 100


class MainActivity : AppCompatActivity() {
    private lateinit var speakButton: Button
    private lateinit var speechTextView: TextView
    private lateinit var intent: Intent
    private lateinit var nowPlayingTextView: TextView
    private lateinit var audioSeekBar: SeekBar
    private lateinit var timerTextView: TextView
    private lateinit var mediaHandler: MediaHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initializing all UI Elements
        speakButton = findViewById(R.id.speakButton)
        speechTextView = findViewById(R.id.speechTextView)
        nowPlayingTextView = findViewById(R.id.nowPlayingTextView)
        audioSeekBar = findViewById(R.id.audioSeekBar)
        timerTextView = findViewById(R.id.timerTextView)

        //Initialize media Player
        mediaHandler = MediaHandler()
        mediaHandler.prepareMediaPlayer(this)



        audioSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "MainActivity $progress")
                val progressPercent: Float = (progress.toFloat() / 100)
                mediaHandler.mediaSetSeek(progressPercent)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    fun speak(view: View) {
        intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now")
        startActivityForResult(intent, PROMPTSPEECH)
    }

    fun fileSelect(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "audio/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, PICKAUDIOFILE)

    }


    fun setTimerTextViewText(text: String) {
        timerTextView.text = text
    }

    fun setNowPlayingTextViewText(text: String) {
        nowPlayingTextView.text = text
    }

    fun setSpeechTextViewText(text: String) {
        speechTextView.text = text
    }

    fun mediaButtonOnClick(view: View) {
        if (this::mediaHandler.isInitialized) {
            mediaHandler.mediaPlayPause()
        }
    }

    fun switchToNoteView(view: View) {
        val intent = Intent(this, NoteViewActivity::class.java)
        intent.putExtra("text", speechTextView.text)
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Speech API control
        if (requestCode == PROMPTSPEECH && resultCode == RESULT_OK) {
            if (data != null) {
                val pastText: String = speechTextView.text.toString()
                setSpeechTextViewText(
                    pastText + "\n" + timerTextView.text + "  " + data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS
                    )!!.get(0)
                )

            }
        }
        //Controls audio file selection
        if (requestCode == PICKAUDIOFILE && resultCode == RESULT_OK) {
            if (data != null) {
                data?.data?.let { selectedUri ->
                    mediaHandler.setMediaDataSource(selectedUri)
                    setNowPlayingTextViewText(getContentFileName(selectedUri))
                }

            }
        }

    }

    //Scans uri for its displayName
    private fun getContentFileName(uri: Uri): String {
        var fileName = "Unknown"
        val cursor: Cursor? = contentResolver.query(
            uri, null, null, null, null, null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                fileName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return fileName
    }
}



