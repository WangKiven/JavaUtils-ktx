package com.kiven.jutil

fun String.isPhone() = matches(Regex("1\\d{10}"))
fun String.isIP() = matches(Regex("((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}"))

object KUtil {

}