package com.example.spinwheel.data

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class SpinWheelApi(
    private val client: OkHttpClient = OkHttpClient()
) {

    @Throws(IOException::class)
    fun fetchText(url: String): String =
        execute(url) { it.string() }

    @Throws(IOException::class)
    fun fetchBytes(url: String): ByteArray =
        execute(url) { it.bytes() }

    private inline fun <T> execute(
        url: String,
        crossinline readBody: (okhttp3.ResponseBody) -> T
    ): T {

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return client.newCall(request)
            .execute()
            .use { response ->

                if (!response.isSuccessful) {
                    throw IOException("Request failed: HTTP ${response.code} for $url")
                }

                readBody(response.body)
            }
    }
}