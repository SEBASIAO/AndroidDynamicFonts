package com.example.testfontdynamic

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class MainActivity : AppCompatActivity() {

    private var selectedFont = AvailableFonts.Deutsch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dynamicTextView = findViewById<TextView>(R.id.textView2)
        val downloadButton = findViewById<Button>(R.id.button)
        val resetButton = findViewById<Button>(R.id.button2)
        val spinner = findViewById<Spinner>(R.id.spinner)

        setupSpinner(spinner)

        downloadButton.setOnClickListener {
            downloadFontFromFirebase(this, "selectedFont.ttf", selectedFont) { font ->
                font?.let {
                    applyCustomFont(dynamicTextView, it)
                }
            }
        }
        resetButton.setOnClickListener {
            dynamicTextView.typeface = Typeface.DEFAULT
        }
    }

    private fun setupSpinner(spinner: Spinner) {
        val options = AvailableFonts.entries.map { it.name }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedOption = parent.getItemAtPosition(position).toString()
                selectedFont = AvailableFonts.valueOf(selectedOption)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        spinner.adapter = adapter
    }
}

fun downloadFontFromFirebase(
    context: Context,
    fileName: String,
    selectedFont: AvailableFonts,
    onComplete: (File?) -> Unit,
) {
    val storageRef = FirebaseStorage.getInstance().reference
    val pathRef = storageRef.child("fonts/${selectedFont.toFontPath()}") // Assuming the file is in a "fonts" folder
    val localFile = File(context.cacheDir, fileName)

    pathRef.getFile(localFile).addOnSuccessListener {
        // Font file downloaded successfully
        onComplete(localFile)
    }.addOnFailureListener { exception ->
        // Handle any errors
        exception.printStackTrace()
        onComplete(null)
    }
}


fun applyCustomFont(
    textView: TextView,
    fontFile: File,
) {
    val typeface = Typeface.createFromFile(fontFile)
    textView.typeface = typeface
}

enum class AvailableFonts {
    Deutsch,
    Typewriter,
    SunFlowers,
}

fun AvailableFonts.toFontPath() = when (this) {
    AvailableFonts.Deutsch -> "font3.ttf"
    AvailableFonts.Typewriter -> "font1.ttf"
    AvailableFonts.SunFlowers -> "font2.ttf"
}
