package com.example.developer


import android.content.Context
import android.widget.Toast

open class ToastMessage() {

    public fun s(c: Context?, message: String?) {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show()
    }
}