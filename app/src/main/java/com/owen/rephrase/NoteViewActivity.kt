package com.owen.rephrase

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

private const val TAG = "NoteViewActivity"
private const val CREATE_FILE_REQUEST_CODE = 1

class NoteViewActivity : AppCompatActivity() {

    private lateinit var noteTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.note_view)

        noteTextView = findViewById(R.id.noteTextView)
        noteTextView.text = intent.getStringExtra("text")

    }

    fun exportText(view: View) {

        writeToFile(noteTextView.text.toString())
    }

    private fun writeToFile(data: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "Text/plain"
            putExtra(Intent.EXTRA_TITLE, "Example.txt")
        }
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(noteTextView.text.toString().toByteArray())
                }
            }
        }
    }
}