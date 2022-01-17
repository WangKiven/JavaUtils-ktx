package com.kiven.jutil

import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import java.io.*
import java.lang.Exception
import java.net.URLConnection
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * 文件相关工具
 * Created by kiven on 2016/11/4.
 */
object KFile {
    private var frontTime: Long = 0
    private var timeCount = 0

    /**
     * 通过时间获取唯一标识
     */
    private val timeTag: String
        get() {
            val currTime = System.currentTimeMillis()
            return if (currTime == frontTime) {
                timeCount++
                String.format("%d_%d", currTime, timeCount)
            } else {
                frontTime = currTime
                currTime.toString()
            }
        }


    /**
     * 创建文件
     *
     * @param directory 在此目录下创建文件
     */
    fun createFile(@NotNull directory: File): File? {
        return if (!directory.exists() && !directory.mkdirs()) {
            null
        } else File(directory, timeTag)
    }

    fun createFile(@NotNull suffix: String, @NotNull directory: File): File? {
        return if (!directory.exists() && !directory.mkdirs()) {
            null
        } else File(directory, timeTag + suffix)
    }

    fun createFile(@NotNull prefix: String, @NotNull suffix: String, @NotNull directory: File): File? {
        return if (!directory.exists() && !directory.mkdirs()) {
            null
        } else File(
            directory,
            prefix + "-" + timeTag + suffix
        )
    }

    fun createNameFile(@NotNull fileName: String?, @NotNull directory: File): File? {
        return if (!directory.exists() && !directory.mkdirs()) {
            null
        } else File(directory, fileName)
    }

    /**
     * 获取文件类型
     * 参考文档：
     * android、java中判断图片文件的格式：http://blog.csdn.net/kehengqun1/article/details/49252549
     * 通过文件头标识判断图片格式：http://zjf30366.blog.163.com/blog/static/41116458201042194542973/
     * gif 格式图片详细解析：http://blog.csdn.net/wzy198852/article/details/17266507
     * JPG文件结构分析：http://blog.csdn.net/hnllei/article/details/6972858
     */
    fun checkFileType(@NotNull file: File?): FileType {
        var fileType = FileType.UNKNOWN
        try {
            val inputStream = FileInputStream(file)
            val flags = IntArray(8)
            flags[0] = inputStream.read()
            flags[1] = inputStream.read()
            if (flags[0] == 255 && flags[1] == 216) { // JPG检测
                inputStream.skip((inputStream.available() - 2).toLong())
                flags[2] = inputStream.read()
                flags[3] = inputStream.read()
                if (flags[2] == 255 && flags[3] == 217) {
                    fileType = FileType.JPG
                }
            } else if (flags[0] == 71 && flags[1] == 73) { // GIF
                flags[2] = inputStream.read()
                flags[3] = inputStream.read()
                inputStream.skip((inputStream.available() - 1).toLong())
                flags[4] = inputStream.read()
                if (flags[2] == 70 && flags[3] == 56 && flags[4] == 0x3B) {
                    fileType = FileType.GIF
                }
            } else if (flags[0] == 0x89 && flags[1] == 0x50) { // PNG
                flags[2] = inputStream.read()
                flags[3] = inputStream.read()
                flags[4] = inputStream.read()
                flags[5] = inputStream.read()
                flags[6] = inputStream.read()
                flags[7] = inputStream.read()
                if (flags[2] == 0x4e && flags[3] == 0x47 && flags[4] == 0x0d && flags[5] == 0x0a && flags[6] == 0x1a && flags[7] == 0x0a) {
                    fileType = FileType.PNG
                }
            }

//            StringBuilder sb = new StringBuilder(file.getName() + ":");
//            for(int i: flags) {
//                sb.append(" ").append(i);
//            }
//            KLog.i(sb.toString());
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fileType
    }

    /**
     * 获取或创建文件后缀名
     */
    fun getPrefix(path: String?): String {
        if (path == null || path.isBlank()) {
            return ""
        }
        val file = File(path)
        val fileName = file.name
        return if (fileName.contains(".")) {
            fileName.substring(fileName.lastIndexOf(".") + 1)
        } else {
            val fileType = checkFileType(file)
            when (fileType) {
                FileType.GIF -> "gif"
                FileType.JPG -> "jpg"
                FileType.PNG -> "png"
                FileType.UNKNOWN -> ""
                else -> ""
            }
        }
    }

    /**
     * Get the Mime Type from a File
     *
     * @param fileName 文件名
     * @return 返回MIME类型
     * thx https://www.oschina.net/question/571282_223549
     */
    @Nullable
    private fun getMimeType(fileName: String): String {
        return URLConnection.getFileNameMap().getContentTypeFor(fileName)
    }

    /**
     * 根据文件后缀名判断 文件是否是视频文件
     *
     * @param fileName 文件名
     * @return 是否是视频文件
     */
    fun isVedioFile(fileName: String): Boolean {
        val mimeType = getMimeType(fileName)
        return fileName.isNotEmpty() && mimeType != null && mimeType.contains("video/")
    }

    /**
     * 保存文件
     */
    fun saveFile(@NotNull file: File, @NotNull data: ByteArray?): Boolean {
        if (file.exists()) {
            file.delete()
        }
        try {
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(data)
            fileOutputStream.close()
            /*ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();*/
        } catch (e: Exception) {
            println(e)
            return false
        }
        return true
    }

    /**
     * 读取文件
     */
    @Throws(Exception::class)
    fun readFileByte(@NotNull file: File): ByteArray {
        if (!file.exists()) {
            throw FileNotFoundException()
        }
        val fileInputStream = FileInputStream(file)
        val buf = ByteArray(fileInputStream.available())
        fileInputStream.read(buf)
        fileInputStream.close()
        return buf
    }

    /**
     * 读取纯文本文件
     */
    fun readFile(filePath: String?): String {
        return readFile(File(filePath))
    }

    fun readFile(file: File): String {
        return readFile(file, StandardCharsets.UTF_8)
    }

    fun readFile(filePath: String?, charset: Charset?): String {
        return readFile(File(filePath), charset)
    }

    fun readFile(file: File, charset: Charset?): String {
        /*InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;

        InputStream inputStream = new FileInputStream(new File(filePath));
        inputReader = new InputStreamReader(inputStream);
        bufferReader = new BufferedReader(inputReader);

        String line = null;
        StringBuffer strBuffer = new StringBuffer();
        while ((line = bufferReader.readLine()) != null) {
            strBuffer.append(line);
        }
        bufferReader.close();

        return new String(strBuffer);*/
        if (file.exists() && file.isFile && file.length() > 0) {
            try {
                val inputStream = FileInputStream(file)
                val l: Int = inputStream.available()
                val bytes = ByteArray(l)
                val r: Int = inputStream.read(bytes)
                println("文件长度：" + file.length() + ", 数据流长度：" + l + ", 读取数据返回值：" + r)
                inputStream.close()
                return String(bytes, charset!!)
            } catch (e: Exception) {
                println(e)
            }
        }
        return ""
    }

    // TODO -----------------文件判断-----------------
    enum class FileType {
        UNKNOWN, JPG, PNG, GIF
    }
}