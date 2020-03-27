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
        /*val var2 = var0.toByteArray(charset(var1!!))
        val var3 = var2.size
        val var5 = CharArray(3 * var3)
        var var6 = 0
        for (var7 in 0 until var3) {
            if ((var2[var7] < 97 || var2[var7] > 122) && (var2[var7] < 65 || var2[var7] > 90) && (var2[var7] < 48 || var2[var7] > 57) && "=,+;.'-@&/\$_()!~*:".indexOf(
                    var2[var7].toChar()
                ) < 0
            ) {
                var5[var6++] = '%'
                var5[var6++] = Character.forDigit(15 and var2[var7].toInt() ushr 4, 16)
                var5[var6++] = Character.forDigit(15 and var2[var7].toInt(), 16)
            } else {
                var5[var6++] = var2[var7].toChar()
            }
        }
        return String(var5, 0, var6)*/
        return if (var1 == null) {
            URLEncoder.encode(var0)
        } else URLEncoder.encode(var0, var1)
    }
}