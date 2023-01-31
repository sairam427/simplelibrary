package com.example.developer

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class DIsplayActivity: AppCompatActivity(){

    fun launch(c: Context){
        var inte= Intent(c,LibraryActivity::class.java)
        startActivity(inte)
    }

    companion object {
        private val counties: HashMap<String, String> = hashMapOf(
            "Mombasa" to "001",
            "Kwale" to "002",
            "Kilifi" to "003",
            "Tana River" to "004",
            "Lamu" to "005",
            "Taita Taveta" to "006"
        )
    }
    fun getCountyName(code: String): String {
        var county: String? = null
        counties.forEach {
            if (it.value == code) {
                county = it.key
            }
        }
        return county ?: "not found"
    }
}