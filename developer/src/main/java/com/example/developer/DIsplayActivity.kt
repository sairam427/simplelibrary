package com.example.developer

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class DIsplayActivity: AppCompatActivity(){

    fun launch(c: Context){
        var inte= Intent(c,LibraryActivity::class.java)
        startActivity(inte)
    }
}