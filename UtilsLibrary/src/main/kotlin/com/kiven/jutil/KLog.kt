package com.kiven.jutil

import java.io.File
import java.lang.management.ManagementFactory
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.text.DateFormat
import java.util.*


object KLog {

    private val toG = fun(b: Long): Double {
        return b * 1.0 / 1024 / 1024 / 1024
    }

    fun printDeviceInfo() {
        val builder = StringBuilder()

        builder.append("\n\ncup核数：").append(Runtime.getRuntime().availableProcessors())
        builder.append("\n程序启动时间：")
            .append(DateFormat.getDateTimeInstance().format(Date(ManagementFactory.getRuntimeMXBean().startTime)))

        builder.append("\n\n>>>>>>>>>>磁盘情况 mac系统不全")
        File.listRoots().forEach {
            builder.append("\n${it.path}: 总计 ${toG(it.totalSpace)}G")
                .append(" 可用${toG(it.usableSpace)}G")
                .append(" 空闲${toG(it.freeSpace)}G")
        }

        builder.append("\n\n>>>>>>>>>>system properties")
        for (property in System.getProperties()) {
            builder.append("\n").append(property.key).append(":\t")
                .append(property.value)
        }
        builder.append("\n\n>>>>>>>>>>system env")
        for (env in System.getenv()) {
            builder.append("\n").append(env.key).append(":\t")
                .append(env.value)
        }

        println(builder)
        printMemoryInfo()
    }

    fun printMemoryInfo() {
        val builder = StringBuilder()

        val mb = ManagementFactory.getMemoryMXBean()
        builder.append("\n\n堆内存使用：")
            .append(toG(mb.heapMemoryUsage.used))
            .append("G 最大：${toG(mb.heapMemoryUsage.max)}G")
            .append(" 提交的：${toG(mb.heapMemoryUsage.committed)}G")
        builder.append("\n非堆内存使用：")
            .append(toG(mb.nonHeapMemoryUsage.used))
            .append("G 最大：${toG(mb.nonHeapMemoryUsage.max)}G")
            .append(" 提交的：${toG(mb.nonHeapMemoryUsage.committed)}G")

        val pmbs = ManagementFactory.getMemoryPoolMXBeans()
        builder.append("\n\n内存池使用情况")
        pmbs.forEach {
            builder
                .append("\n内存池名称：${it.name}, 类型: ${it.type}")
                // 这两个报了异常
//                .append(", 内存池的使用阈值:${toG(it.usageThreshold)}")
//                .append(", 超过使用阈值的次数:${toG(it.usageThresholdCount)}")
                .append(", 已使用：${toG(it.usage.used)}")
        }

        val mmbs = ManagementFactory.getMemoryManagerMXBeans()
        builder.append("\n\n内存列表")
        for (mmb in mmbs) {
            builder.append("\nname:${mmb.name}")
                .append(" 包含内存池：${mmb.memoryPoolNames.joinToString()}")
        }

        val tb = ManagementFactory.getThreadMXBean()
        builder.append("\n\n线程情况")
            .append("线程计数：${tb.totalStartedThreadCount} ${tb.threadCount} ${tb.daemonThreadCount} ${tb.peakThreadCount}")



        println(builder)
    }


    /**
     * 打印属性
     * obj, cla 传一个就行了。仅有cla就仅获取静态属性值。两个都有则cla = obj.getClass();
     *
     * @param isDeclared getFields() or getDeclaredFields()
     */
    fun printClassField(obj: Any?, cl: Class<*>?, isDeclared: Boolean) {
        var cla = cl
        /*if (!isDebug()) return
        if (obj == null && cla == null) {
            Log.i(tag, "对象实例皆为null")
            return
        }*/
        var objCode = 0
        if (obj != null) {
            objCode = obj.hashCode()
            cla = obj.javaClass
        }
        // getFields()：获得某个类的所有的公共（public）的字段，包括父类中的字段。
// getDeclaredFields()：获得某个类的所有声明的字段，即包括public、private和proteced，但是不包括父类的申明字段。
        val fields: Array<Field>
        fields = if (isDeclared) cla!!.declaredFields else cla!!.fields
        val sb =
            java.lang.StringBuilder("对象/实例 " + cla.name + "(" + objCode + ")" + "的属性有(" + fields.size + ")：")
        for (field in fields) {
            sb.append("\n").append(field.name).append(": ").append(field.getType().getSimpleName())
            val modifiers: Int = field.modifiers
            if (!Modifier.isPublic(modifiers)) field.isAccessible = true
            if (Modifier.isStatic(modifiers)) {
                try {
                    val value = field.get(cla)
                    if (value == null) {
                        sb.append(" = null")
                    } else sb.append(" = ").append(value)
                } catch (e: Exception) {
                    e.printStackTrace()
                    sb.append(" 值获取异常")
                }
            } else {
                if (obj == null) {
                    sb.append("不是静态属性")
                } else {
                    try {
                        val value = field.get(obj)
                        if (value == null) {
                            sb.append(" = null")
                        } else sb.append(" = ").append(value)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        sb.append(" 值获取异常")
                    }
                }
            }
        }
        println(sb)
    }
}