package com.example.developer

import android.content.Context

open class SingletonCreator<out T : Any>(val creator: (Context) -> T) {
    @Volatile
    private var instance: T? = null
    fun with(context: Context): T =
        instance ?: synchronized(this) { instance ?: creator(context).apply { instance = this } }
}