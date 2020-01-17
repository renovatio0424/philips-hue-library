package com.reno.philipshue.bridge.remote.repository.token

import android.net.Uri
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.reno.philipshue.Log
import com.reno.philipshue.bridge.remote.CALLBACK_URL
import com.reno.philipshue.model.Token
import org.koin.java.KoinJavaComponent.inject

class HueWebViewClient(private val doAfterCallback: () -> Unit) : WebViewClient() {
    private val tokenRepository: ITokenRepository by inject(ITokenRepository::class.java)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        if (request != null)
            "${request.url}\n${request.isForMainFrame}\n${request.method}\n${request.requestHeaders}".Log()

        if (request != null && isValidCallbackUrl(request.url)) {
            val code = request.url.getQueryParameter("code")
            val state = request.url.getQueryParameter("state")

            if (code != null && state != null){
                tokenRepository.saveToken(Token(code, state))
            }

            doAfterCallback.invoke()
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
}

