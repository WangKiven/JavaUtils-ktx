package com.kiven.jutil

import java.io.UnsupportedEncodingException
import java.net.MalformedURLException
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Created by oukobayashi on 2020/3/13.
 */
object KUrlUtil {
    @Throws(MalformedURLException::class)
    fun decode(var0: String): String {
        return try {
            decode(var0, "UTF-8")
        } catch (var2: UnsupportedEncodingException) {
            throw MalformedURLException("ISO-Latin-1 decoder unavailable")
        }
    }

    @Throws(MalformedURLException::class, UnsupportedEncodingException::class)
    fun decode(var0: String, var1: String?): String {
        return try {
            URLDecoder.decode(var0, var1)
        } catch (var4: IllegalArgumentException) {
            val var3 = MalformedURLException("Invalid URI encoding: $var0")
            var3.initCause(var4)
            throw var3
        }
    }

    @Throws(UnsupportedEncodingException::class)
    fun encode(var0: String, var1: String?): String {

        // UriUtils.encode(message.argument, "UTF-8") //UriUtils.encode是spring提供的方法
        return if (var1 == null) {
            URLEncoder.encode(var0)
        } else URLEncoder.encode(var0, var1)
    }
}