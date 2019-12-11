
package com.example.uav_client.Utils

import android.os.Handler
import android.os.Looper

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors internal constructor(private val networkIO: Executor, private val mainThread: Executor,private val single:Executor) {

    constructor() : this(Executors.newFixedThreadPool(THREAD_COUNT),MainThreadExecutor(),Executors.newSingleThreadExecutor())

    fun networkIO(): Executor {
        return networkIO
    }

    fun single(): Executor {
        return single
    }

    fun mainThread(): Executor {
        return mainThread
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    companion object {
        private val THREAD_COUNT = 3
    }
}
