package com.kiven.jutil

object KUtil {
}
fun String.isPhone() = matches(Regex("1\\d{10}"))