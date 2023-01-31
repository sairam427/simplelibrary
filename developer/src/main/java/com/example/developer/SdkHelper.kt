package com.example.developer

import android.content.Context
import android.content.Intent

internal class SdkHelper private constructor(var context: Context) : DpSdkHelper() {
    companion object : SingletonCreator<DpSdkHelper>(::SdkHelper) {
        private const val TAG: String = "SdkHelper"
    }

    override fun launchPrefr() {
        val intent = Intent(
            context,
            LibraryActivity::class.java
        )
        context.startActivity(intent)
    }
}