package com.example.uav_client.Application

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.amap.api.maps.model.LatLng
import com.example.uav_client.Data.Common.User
import com.example.uav_client.Network.Threads
import java.util.HashMap

class SysApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    companion object {
        @JvmStatic
        var user: User = User("1", "", User.SUPER_USER, 1, User.SUPER_USER)
        @JvmStatic
        var alarmArea: MutableList<LatLng> = ArrayList()
        var datahash: MutableList<String> = ArrayList()
        @JvmStatic
        var stationItem: MutableList<String> = ArrayList()
        lateinit var activity1:BaseActivity
    }

    private val activityLifecycleCallbacks = object : ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {
            activity1 = activity as BaseActivity
            Log.d("ddadad","onActivityCreated")
        }

        override fun onActivityStarted(activity: Activity) {

        }

        override fun onActivityResumed(activity: Activity) {

        }

        override fun onActivityPaused(activity: Activity) {
            activity1 = Activity() as BaseActivity
            Log.d("ddadad","onActivityPaused")
        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {

        }
    }

    override fun onTerminate() {
        //注销这个接口。
        unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
        Threads.exit()
        super.onTerminate()
    }
}