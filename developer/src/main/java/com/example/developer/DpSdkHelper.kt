package com.example.developer

import android.content.Context

abstract class DpSdkHelper {
    abstract fun launchPrefr()

    companion object Factory {
        fun get(context: Context): DpSdkHelper = SdkHelper.with(context)
    }
}