package com.example.developer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.MailTo
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider

class LibraryActivity : AppCompatActivity() {




    //weview
    private var mWebView: WebView? = null

    //Implecit intent request codes
    private val REQUEST_CODE_PERMISSIONS = 3
    private val FCR = 1
    private val FCR_BELOW5 = 2

    //Callbacks for returning the data to webview
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var mUM: android.webkit.ValueCallback<Uri>? = null

    //File paths related.
    private val TYPE = "*/*"
    private var mCameraPhotoPath: String? = null

    //Mandatory permissions for the loan journey
    private val mandatoryPermissions = arrayOf<String>(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )

    //These two variables on used after permissions enable callback
    private var mIsCaptureEnabled = false
    private var mSelectedRequestCode = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        mWebView = findViewById<android.webkit.WebView>(R.id.webView)

        enableWebViewSettings()
        mWebView?.loadUrl("https://test-cvloan.prefr.com/prefr")
    }

    fun closeActivity() {
        finish()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun enableWebViewSettings() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            mWebView?.settings?.mixedContentMode = 0
            mWebView?.setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
            android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true)
        } else if (android.os.Build.VERSION.SDK_INT >= 19) {
            mWebView?.setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
            android.webkit.CookieManager.getInstance().setAcceptCookie(true)
        } else {
            mWebView?.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
            android.webkit.CookieManager.getInstance().setAcceptCookie(true)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        mWebView?.settings?.domStorageEnabled = true
        mWebView?.settings?.allowFileAccess = true

        mWebView?.clearCache(true)
        mWebView?.clearHistory()
        mWebView?.settings?.setSupportMultipleWindows(true)
        mWebView?.settings?.javaScriptEnabled = true
        mWebView?.settings?.javaScriptCanOpenWindowsAutomatically = true

        mWebView?.webViewClient = object : android.webkit.WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: android.webkit.WebView,
                url: String
            ): Boolean {
                if (url.trim { it <= ' ' }.contains("webviewclose=true")) {
                    closeActivity()
                } else if (url.startsWith("mailto:")) {
                    val mt: MailTo = MailTo.parse(url)
                    val mailsTo = mt.to ?: ""
                    val subject = mt.subject ?: ""
                    val body = mt.body ?: ""
                    val mailsCC = mt.cc ?: ""
                    val i: android.content.Intent =
                        getEmailIntent(mailsTo, subject, body, mailsCC)
                    startActivity(i)
                    return true
                } else {
                    view.loadUrl(url)
                }
                return true
            }

            @SuppressLint("IntentReset")
            private fun getEmailIntent(
                address: String,
                subject: String,
                body: String,
                cc: String
            ): android.content.Intent {
                val intent: android.content.Intent =
                    android.content.Intent(android.content.Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:");
                intent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf(address))
                intent.putExtra(android.content.Intent.EXTRA_TEXT, body)
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
                intent.putExtra(android.content.Intent.EXTRA_CC, cc)
                return intent
            }

            override fun onPageStarted(
                view: android.webkit.WebView,
                url: String,
                favicon: android.graphics.Bitmap?
            ) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: android.webkit.WebView, url: String) {
                super.onPageFinished(view, url)
            }
        }
        mWebView?.webChromeClient = object : android.webkit.WebChromeClient() {
            override fun onCreateWindow(
                view: android.webkit.WebView,
                dialog: Boolean,
                userGesture: Boolean,
                resultMsg: android.os.Message
            ): Boolean {

                val popUpWebView = WebView(this@LibraryActivity)
                popUpWebView!!.settings.javaScriptEnabled = true
                popUpWebView!!.settings.setSupportZoom(true)
                popUpWebView!!.settings.builtInZoomControls = true
                popUpWebView!!.settings.setSupportMultipleWindows(true)
                popUpWebView!!.settings.javaScriptCanOpenWindowsAutomatically = true
                view.addView(
                    popUpWebView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = popUpWebView
                resultMsg.sendToTarget()
                popUpWebView!!.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        return if(view.url?.contains("herofincorp.com") == true){
                            false
                        }else{
                            view.loadUrl(url)
                            true
                        }
                    }

                    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {

                        super.onPageStarted(view, url, favicon)
                    }

                    override fun onPageFinished(view: WebView, url: String) {
                        super.onPageFinished(view, url)
                        if (popUpWebView != null && url.contains("verfied-documents")) {
                            view.removeView(popUpWebView)
                        }
                    }
                }
                popUpWebView!!.webChromeClient = object : WebChromeClient() {
                    override fun onCloseWindow(window: WebView) {
                        super.onCloseWindow(window)
                        if (popUpWebView != null) {
                            view.removeView(popUpWebView)
                        }
                    }
                }
                return true
            }

            //For Android 4.1+
            fun openFileChooser(
                uploadMsg: android.webkit.ValueCallback<android.net.Uri>,
                acceptType: String?,
                capture: String?
            ) {
                mUM = uploadMsg
                val isCaptureEnabled: Boolean = !android.text.TextUtils.isEmpty(capture)
                selectDocument(isCaptureEnabled, FCR_BELOW5)
            }

            //For Android 5.0+
            override fun onShowFileChooser(
                webView: android.webkit.WebView,
                filePathCallback: android.webkit.ValueCallback<Array<android.net.Uri>>,
                fileChooserParams: android.webkit.WebChromeClient.FileChooserParams
            ): Boolean {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    mFilePathCallback?.onReceiveValue(null)
                    mFilePathCallback = filePathCallback
                    mIsCaptureEnabled = fileChooserParams.isCaptureEnabled()
                    mSelectedRequestCode = FCR
                    if (isRequiredPermissionsEnabled()) {
                        selectDocument(
                            fileChooserParams.isCaptureEnabled(),
                            FCR
                        )
                    }
                }
                return true
            }
        }
    }

    private fun isRequiredPermissionsEnabled(): Boolean {
        var isAllPermissionsGranted = true
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            for (p in mandatoryPermissions) {
                isAllPermissionsGranted =
                    checkSelfPermission(p) === android.content.pm.PackageManager.PERMISSION_GRANTED
                if (!isAllPermissionsGranted) break
            }
            if (!isAllPermissionsGranted) requestPermission(
                mandatoryPermissions,
                REQUEST_CODE_PERMISSIONS
            )
        }
        return isAllPermissionsGranted
    }

    private fun requestPermission(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(
            this@LibraryActivity,
            mandatoryPermissions,
            REQUEST_CODE_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, @NonNull permissions: Array<String?>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0) {
            var allPermissionsSuccess = true
            //val failedMandatoryPermissionsList: Array<String?> = ArrayList()
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsSuccess = false
                }
            }
            // Loop through unless granted
            if (!allPermissionsSuccess) {
                // User selected "Don't ask again"
                Toast.makeText(
                    this@LibraryActivity,
                    "Please enable required permission to move forward",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // All OK. Go ahead
                selectDocument(mIsCaptureEnabled, mSelectedRequestCode)
            }
        }
    }

    private fun selectDocument(isCaptureEnabled: Boolean, requestCode: Int) {
        if (isCaptureEnabled) {
            callCamera(requestCode)
        } else {
            callCameraAndGallery(requestCode)
        }
    }

    private fun callCamera(requestCode: Int) {
        val cameraIntent: android.content.Intent? = getCameraIntent()
        if (cameraIntent != null) {
            startActivityForResult(cameraIntent, requestCode)
        }
    }

    private fun getCameraIntent(): android.content.Intent? {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var photoFile: java.io.File? = null
        try {
            photoFile = createImageFile()
            takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath)
        } catch (ex: java.io.IOException) {
            //android.util.Log.e(TAG, "Image file creation failed", ex)
        }
        if (photoFile != null) {
            mCameraPhotoPath = "file:" + photoFile.getAbsolutePath()
            val photoURI: android.net.Uri = FileProvider.getUriForFile(
                this@LibraryActivity,
                this@LibraryActivity.getApplicationContext().getPackageName()
                    .toString() + ".provider",
                photoFile
            )
            takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI)
            takePictureIntent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        // }
        return takePictureIntent
    }

    private fun callCameraAndGallery(requestCode: Int) {
        val takePictureIntent: android.content.Intent? = getCameraIntent()
        val intentArray: Array<android.content.Intent?>
        var contentSelectionIntent: android.content.Intent? = null
        contentSelectionIntent = android.content.Intent(android.content.Intent.ACTION_GET_CONTENT)
        contentSelectionIntent.addCategory(android.content.Intent.CATEGORY_OPENABLE)
        contentSelectionIntent.setType(TYPE)
        intentArray = arrayOf<android.content.Intent?>(takePictureIntent)
        val chooserIntent: android.content.Intent =
            android.content.Intent(android.content.Intent.ACTION_CHOOSER)
        chooserIntent.putExtra(android.content.Intent.EXTRA_INTENT, contentSelectionIntent)
        chooserIntent.putExtra(android.content.Intent.EXTRA_TITLE, "Image Chooser")
        chooserIntent.putExtra(android.content.Intent.EXTRA_INITIAL_INTENTS, intentArray)
        startActivityForResult(chooserIntent, requestCode)
    }

    // Create an image file
    private fun createImageFile(): java.io.File {
        @android.annotation.SuppressLint("SimpleDateFormat") val timeStamp: String =
            java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
        val imageFileName = "img_" + timeStamp + "_"
        //It is for internal storage i.e data/data/{package_name}/files
        val storageDir: java.io.File = getFilesDir()
        return java.io.File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    protected override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: android.content.Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == FCR) {
            var results: Array<Uri>? = null
            if (resultCode == android.app.Activity.RESULT_OK) {
                if (null == mFilePathCallback) {
                    return
                }
                if (intent?.data == null) {
                    //Capture Photo if no image available
                    if (mCameraPhotoPath != null) {
                        results = arrayOf<android.net.Uri>(android.net.Uri.parse(mCameraPhotoPath))
                    }
                } else {
                    val dataString: String? = intent.dataString
                    if (dataString != null) {
                        results = arrayOf<android.net.Uri>(android.net.Uri.parse(dataString))
                    }
                }
            }
            mFilePathCallback!!.onReceiveValue(results)
            mFilePathCallback = null
        } else if (requestCode == FCR_BELOW5) {
            if (null == mUM) return
            val result: android.net.Uri? =
                if (intent == null || resultCode != RESULT_OK) null else intent.data
            mUM!!.onReceiveValue(result)
            mUM = null
        }
    }
}