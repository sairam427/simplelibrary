package com.example.developer

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class LibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        var webview=findViewById<WebView>(R.id.webView)

        webview.webViewClient = WebViewClient()
        webview.loadUrl("https://test-cvloan.prefr.com/prefr/GetStarted?startPage=base")
        webview.settings.javaScriptEnabled = true
        webview.settings.setSupportZoom(true)
    }
}