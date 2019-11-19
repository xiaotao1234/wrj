package com.example.uav_client.Base

interface BaseView {
    fun release()//必须在结束时释放presenter引用来避免内存泄漏
}