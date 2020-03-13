package com.kiven.jutil

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

object KProperties {
    private var filePath: String? = null
    private val properties by lazy { Properties() }

    @Synchronized
    fun setDir(dir: String) {
        if (filePath != null) return

        val file = File(dir)
        if (!file.exists()) {
            file.mkdirs()
        }

        if (file.exists() && file.isDirectory) filePath = dir + File.pathSeparator + "pushLibraryInfo.properties"
        else return

        var inputStream: FileInputStream? = null
        try {
            inputStream = FileInputStream(filePath)
            properties.load(inputStream)
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    @Synchronized fun <T> put(key: String, value: T) {
        properties[key] = value

        if (filePath == null) return

        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(filePath)
            properties.store(outputStream, "项目可变动配置文件")
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            try {
                outputStream?.close()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun getInt(key: String, default: Int): Int {
        val value = properties[key]

        return if (value is Int) value
        else default
    }

    fun getLong(key: String, default: Long): Long {
        val value = properties[key]

        return if (value is Long) value
        else default
    }

    fun getString(key: String, default: String): String {
        val value = properties[key]

        return if (value is String) value
        else default
    }

    fun getBoolean(key: String, default: Boolean): Boolean {
        val value = properties[key]

        return if (value is Boolean) value
        else default
    }
}