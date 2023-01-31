package com.example.developer

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity

object Sydney:AppCompatActivity(){
    val suburbs = listOf("Ryde", "Chippendale")

    fun launch(c: Context){
        var inte= Intent(c,LibraryActivity::class.java)
        startActivity(inte)
    }
}