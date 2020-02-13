package com.kiven.jutil

object KSyncFun {
    fun run(runId:Int, method:()->Unit) {
        when(runId){
            0 -> run0(method)
            1 -> run1(method)
            2 -> run2(method)
            3 -> run3(method)
            4 -> run4(method)
            5 -> run5(method)
            6 -> run6(method)
            7 -> run7(method)
            8 -> run8(method)
            9 -> run9(method)
            else -> run10(method)
        }
    }

    @Synchronized
    fun run0(method:()->Unit) {
        method()
    }

    @Synchronized
    fun run1(method:()->Unit) {
        method()
    }

    @Synchronized
    fun run2(method:()->Unit) {
        method()
    }

    @Synchronized
    fun run3(method:()->Unit) {
        method()
    }

    @Synchronized
    fun run4(method:()->Unit) {
        method()
    }

    @Synchronized
    fun run5(method:()->Unit) {
        method()
    }

    @Synchronized
    fun run6(method:()->Unit) {
        method()
    }

    @Synchronized
    fun run7(method:()->Unit) {
        method()
    }

    @Synchronized
    fun run8(method:()->Unit) {
        method()
    }

    @Synchronized
    fun run9(method:()->Unit) {
        method()
    }

    @Synchronized
    fun run10(method:()->Unit) {
        method()
    }
}