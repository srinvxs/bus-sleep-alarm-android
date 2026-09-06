package com.srinivas.bussleepalarm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.srinivas.bussleepalarm.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private var geolocationCallback: GeolocationPermissions.Callback? = null
    private var geolocationOrigin: String? = null

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineLocation =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val coarseLocation =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineLocation || coarseLocation) {
                geolocationCallback?.invoke(
                    geolocationOrigin,
                    true,
                    false
                )
            } else {
                geolocationCallback?.invoke(
                    geolocationOrigin,
                    false,
                    false
                )
            }

            geolocationCallback = null
            geolocationOrigin = null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {

                AndroidView(
                    modifier = Modifier.fillMaxSize(),

                    factory = { context ->

                        WebView(context).apply {

                            webViewClient = WebViewClient()

                            settings.javaScriptEnabled = true
                            settings.setGeolocationEnabled(true)

                            webChromeClient = object : WebChromeClient() {

                                override fun onGeolocationPermissionsShowPrompt(
                                    origin: String?,
                                    callback: GeolocationPermissions.Callback?
                                ) {
                                    geolocationOrigin = origin
                                    geolocationCallback = callback

                                    if (
                                        checkSelfPermission(
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        ) == PackageManager.PERMISSION_GRANTED ||
                                        checkSelfPermission(
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        ) == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        callback?.invoke(
                                            origin,
                                            true,
                                            false
                                        )
                                    } else {
                                        locationPermissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    }
                                }
                            }

                            loadUrl("https://bus-sleep-alarm.vercel.app/")
                        }
                    }
                )
            }
        }
    }
}