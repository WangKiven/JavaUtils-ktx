package com.kiven.jutil

import java.net.HttpURLConnection
import java.net.URL

object KNet {
    // 创建form类型的body。body也可以是json，其实服务器识别的就是个字符串，主要是看服务器怎么解析
    fun createFormBody(param: Map<String, Any?>? = null):String? {
        var body: String? = null
        param?.apply {
            filter { it.value != null }.apply {
                if (size > 0) {
                    body = toList().joinToString("&") { "${it.first}=${it.second}" }
                }
            }
        }
        return body
    }

    fun request(
        url: String,
        body: String? = null,
        requestProperty: Map<String, String?>? = null,
        requestMethod: String = "POST"
    ): Result {
        try {
            val connect = URL(url)
                .openConnection() as HttpURLConnection
            connect.requestMethod = requestMethod

            // 请求头
            requestProperty?.forEach { key, value ->
                if (value != null) {
//                    val uriKey = UrlUtil.encode(key, "UTF-8").toUpperCase()
//                    val uriArgu = UrlUtil.encode(value, "UTF-8").toUpperCase()
//                    connect.addRequestProperty(uriKey, uriArgu)
                    connect.addRequestProperty(key, value)
                }
            }
            // 请求体
            if (body != null) {
                connect.doOutput = true // 使用outputStream前，先确保 doOutput = true
                val os = connect.outputStream
                os.write(body.toByteArray())
                os.close()
            }

            val responseCode = connect.responseCode
            val isError = responseCode >= 400

            val inputStream = if (isError) connect.errorStream else connect.inputStream

            val datas = mutableListOf<Byte>()
            val buffer = ByteArray(512)
            var readLength: Int
            do {
                readLength = inputStream.read(buffer)
                if (readLength > 0) {
                    for (i in 0 until readLength) {
                        datas.add(buffer[i])
                    }
                }
            } while (readLength != -1)

            inputStream.close()

            val result = String(datas.toByteArray())

            return if (isError) {
                Result(responseCode, "响应异常，异常码 = $responseCode, 错误信息：$result", "")
            } else Result(200, "", result)
        } catch (e: Throwable) {
//            return Result(10086, e.message ?: "不明错误", "")
            throw e
        }
    }

    data class Result(val status:Int, val message:String, val data:Any?)
}