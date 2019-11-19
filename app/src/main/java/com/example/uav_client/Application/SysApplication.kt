package com.example.uav_client.Application

import android.app.Application
import com.example.uav_client.Data.Common.User

class SysApplication : Application() {
    internal var firstIn: Boolean = true
    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        @JvmStatic
        var user: User = User("","",User.SUPER_USER,1,0)
    }
}