package com.kiven.spring

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.URLEncoder
import javax.servlet.http.HttpServletResponse
import kotlin.math.max

object ControllerUtil {

    fun readFile(response: HttpServletResponse, file: File, range: String):Boolean {

        // todo 下载文件，参考断点续传文档：https://blog.csdn.net/u012160163/article/details/54967612

        // 计算请求区间
        var start = 0L
        var end = file.length() - 1

        val r = range.replace("bytes=", "").split("-")
        if (r.isNotEmpty()) {
            val s = r[0].toLongOrNull() ?: 0
            start = max(0, s)
        }

        if (start >= end) {
            response.sendError(333, "文件小于开始位置")
            return false
        }

        if (r.size > 1) {
            val e = r[1].toLongOrNull() ?: end

            if (e in (start + 1) until end) {
                end = e
            }
        }


//            response.characterEncoding = "utf-8"
//            response.contentType = "application/force-download"// 设置强制下载不打开
        // 来清除首部的空白行
        response.reset()
        // 告诉客户端允许断点续传多线程连接下载,响应的格式是:Accept-Ranges: bytes
        response.setHeader("Accept-Ranges", "bytes")

        // 设置文件名，中文需要编码
        response.setHeader(
            "Content-Disposition",
            "attachment;fileName=\"${URLEncoder.encode(file.name, "utf-8")}\""
        )

        val buffer = ByteArray(1024)
        var fis: FileInputStream? = null
        var bis: BufferedInputStream? = null
        try {
            fis = FileInputStream(file)
            bis = BufferedInputStream(fis)
            val os = response.outputStream

            // 调到指定位置开始读
            if (start > 0) {
                // 如果是第一次下,还没有断点续传,状态是默认的 200
                // 不是从最开始下载，断点下载响应号为206
                // 响应的格式是: Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
                response.status = 206
                response.setHeader("Content-Range", "bytes $start-$end/${file.length()}")
                response.setHeader("Content-Length", (end - start + 1).toString())

                bis.skip(start)
            }
            println("start = $start, end = $end, len = ${file.length()}")

            var i = bis.read(buffer)
            if (end + 1 >= file.length()) {
                while (i != -1) {
                    os.write(buffer, 0, i)
                    i = bis.read(buffer)
                }
            } else {
                var readPosition = start
                while (i != -1) {
                    readPosition += i
                    if (readPosition - 1 > end) {
                        os.write(buffer, 0, (i - (readPosition - end)).toInt())

                        break
                    } else {
                        os.write(buffer, 0, i)
                    }

                    i = bis.read(buffer)
                }
            }

            fis.close()
            bis.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()

            response.sendError(333, "异常报告：${e.message}")
        } finally {
            if (bis != null) {
                try {
                    bis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            if (fis != null) {
                try {
                    fis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        return false
    }
}