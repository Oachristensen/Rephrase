package com.owen.rephrase

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NoteViewActivity : AppCompatActivity() {
    private lateinit var noteTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.note_view)

        noteTextView = findViewById(R.id.noteTextView)
        noteTextView.text = intent.getStringExtra("text")

    }
}