package com.owen.rephrase

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.CursorLoader
import com.owen.rephrase.handlers.MediaHandler
import java.io.File
import java.net.URI


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

    private val STORAGEDIRECTORY = (Environment.getExternalStorageDirectory().path + "/Rephrase Audios")


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
        val appSpecificDir = getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)
        println(appSpecificDir)

        if (createDirectory(STORAGEDIRECTORY)) {
            println("Directory created successfully")
        } else {
            println("directory already created or failed to create")
        }


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

        if (requestCode == PROMPTSPEECH && resultCode == RESULT_OK) {
            if (data != null) {
                val pastText: String = speechTextView.text.toString()
                speechTextView.text =
                    (pastText + "\n" + timerTextView.text + "  " + data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!!
                        .get(0))
            }
        }
        if (requestCode == PICKAUDIOFILE && resultCode == RESULT_OK) {
            if (data != null) {
                println(data.data)
                data?.data?.let { selectedUri ->
dumpImageMetaData(selectedUri)
                    // I am aware this is bad and I might fix it, but I have been trying to do it the right way for 3 hours and idk
                    val filePathArray = selectedUri.path?.split("/")
                    println(filePathArray?.get(filePathArray.lastIndex))
                }

            }
        }

    }
    fun dumpImageMetaData(uri: Uri) {

        // The query, because it only applies to a single document, returns only
        // one row. There's no need to filter, sort, or select fields,
        // because we want all fields for one document.
        val cursor: Cursor? = contentResolver.query(
            uri, null, null, null, null, null)

        cursor?.use {
            // moveToFirst() returns false if the cursor has 0 rows. Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (it.moveToFirst()) {

                // Note it's called "Display Name". This is
                // provider-specific, and might not necessarily be the file name.
                val displayName: String =
                    it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                Log.i(TAG, "Display Name: $displayName")

                val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                // If the size is unknown, the value stored is null. But because an
                // int can't be null, the behavior is implementation-specific,
                // and unpredictable. So as
                // a rule, check if it's null before assigning to an int. This will
                // happen often: The storage API allows for remote files, whose
                // size might not be locally known.
                val size: String = if (!it.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    it.getString(sizeIndex)
                } else {
                    "Unknown"
                }
                Log.i(TAG, "Size: $size")
                val filePath : String =

            }
        }
    }

    fun createDirectory(directoryPath: String): Boolean {
        val directory = File(directoryPath)
        if (!directory.exists()) {
            return directory.mkdir()
        }
        return false
    }

}


