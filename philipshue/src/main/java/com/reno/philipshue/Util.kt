package com.reno.philipshue


private const val LOG_TAG = "TAG"

fun String.Log(tag: String?) {
    android.util.Log.d(tag ?: LOG_TAG, this)
}

fun String.Log() {
    this.Log("TAG")
}