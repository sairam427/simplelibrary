package com.example.simplelibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Surface
import android.widget.Button
import android.widget.TextView
import com.example.developer.Sydney
import com.example.developer.ToastMessage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var textviews=findViewById<TextView>(R.id.textview)
        textviews.text=Sydney.suburbs[0]

        var buttons=findViewById<Button>(R.id.buttons)
        buttons.setOnClickListener{}
    }
}