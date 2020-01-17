package com.reno.philipshue.bridge.remote.repository.token

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.reno.philipshue.Log
import com.reno.philipshue.bridge.remote.CALLBACK_URL
import com.reno.philipshue.model.CallbackResult
import com.reno.philipshue.model.Token
import org.koin.java.KoinJavaComponent.inject

class HueWebViewClient(private val doAfterCallback: () -> Unit) : WebViewClient(), IHueCallback {
    private val tokenRepository: ITokenRepository by inject(ITokenRepository::class.java)

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        view?.addJavascriptInterface(this, "Android")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        if (request != null)
            "${request.url}\n${request.isForMainFrame}\n${request.method}\n${request.requestHeaders}".Log()

        if (request != null && isValidCallbackUrl(request.url)) {
            view?.loadUrl("javascript:getCallbackResult()")
        }

        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        tokenRepository.getToken().toString().Log()
        super.onPageFinished(view, url)
    }

    private fun isValidCallbackUrl(url: Uri): Boolean {
        return url.toString().contains(CALLBACK_URL)
    }

    @JavascriptInterface
    override fun getCallbackResult(result: String) {
        result.Log()

        val gson = Gson()
        val callbackResult = gson.fromJson(result, CallbackResult::class.java)

        val code = callbackResult.code
        val state = callbackResult.state
        val key = callbackResult.key

        tokenRepository.saveToken(Token(code, state))
        tokenRepository.saveKey(key)

        doAfterCallback.invoke()
    }

}

interface IHueCallback {
    fun getCallbackResult(result: String)
}

