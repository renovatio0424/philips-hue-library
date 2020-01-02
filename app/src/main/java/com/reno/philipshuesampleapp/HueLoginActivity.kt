package com.reno.philipshuesampleapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.reno.philipshue.bridge.remote.authUrl
import kotlinx.android.synthetic.main.activity_hue_login.*

class HueLoginActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hue_login)

        hue_login_web_view.settings.javaScriptEnabled = true
        hue_login_web_view.settings.userAgentString = System.getProperty("http.agent")
        hue_login_web_view.webViewClient = AuthWebClient()
        hue_login_web_view.loadUrl(authUrl)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && hue_login_web_view.canGoBack()) {
            hue_login_web_view.goBack()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    inner class AuthWebClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return false
        }

    }

}