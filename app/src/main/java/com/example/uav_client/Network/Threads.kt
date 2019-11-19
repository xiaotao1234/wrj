package com.example.uav_client.Network

import android.util.Log
import com.example.uav_client.Data.Common.RequestBuildUtil
import com.example.uav_client.Data.DataSS.Datastatic
import com.example.uav_client.Data.Main.DataListSource
import com.example.uav_client.Utils.AppExecutors
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.Socket

class Threads {

    companion object {
        internal var socket: Socket? = null
        lateinit var ous: OutputStream
        lateinit var ins: InputStream
        lateinit var request: ByteArray
        var appExecutors: AppExecutors? = null
        var byteArray: ByteArray = ByteArray(0)
        var messages: MutableList<ByteArray>?=null
        var length: Int = 0
        var isSend: Boolean = false
        var runnable = Runnable {
            while (socket == null) {
                releaseConnect()
                if (messages == null) {
                    messages = ArrayList()
                }
            }
            sendRequest()
            while (true) {
                if(socket==null){
                    releaseConnect()
                }
                synchronized(ins) {
                    length = ins.available()
                    if (length > 0) {
                        byteArray = ByteArray(length)
                        ins.read(byteArray)
                        messages?.add(byteArray)
                        appExecutors?.networkIO()?.execute {
                            Consumer.back(byteArray)
                        }
                    }
                }
                if (length == 0) {
                    Thread.sleep(40)
                }
            }
        }

        private fun sendRequest() {
            if(socket!=null){
                ous.write(request)
                ous.flush()
            }else{

            }
        }

        fun start(request: String, requestCode: Int, appExecutors: AppExecutors,callback:DataListSource.getDataCallBack) {
            this.request = RequestBuildUtil.addFrameHeader(request, requestCode)
            if (this.appExecutors == null) {
                this.appExecutors = appExecutors
            }
            if (socket == null) {
                runnable.run()
            } else {
                sendRequest()
            }
        }

        private fun releaseConnect() {
            Log.d("xiao","release")
            loop@ for (i in 1..3) {
                try {
                    if (socket == null) {
                        socket = Socket(Datastatic.IP, Datastatic.PORT)
                        socket!!.soTimeout = 5000
                        socket!!.keepAlive = true
                        ous = socket!!.getOutputStream()
                        ins = socket!!.getInputStream()
                    } else {
                        break@loop
                    }
                } catch (e: Exception) {
                    Thread.sleep(1000)
                }
            }
        }
    }

}