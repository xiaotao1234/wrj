package com.example.uav_client.Data.DataSS

import com.example.uav_client.Data.Main.DataListSource
import com.example.uav_client.Data.Main.MainDataInfo
import com.example.uav_client.Network.Threads
import com.example.uav_client.Utils.AppExecutors

class dataSchduleImpl : dataSchdule<MainDataInfo> {
    override fun getDataMeM(): ByteArray {
//        Threads.start(request)
        return Threads.byteArray!!
//        Thread.sleep(1000)
//        var list: MutableList<MainDataInfo> = ArrayList()
//        list.add(MainDataInfo("鲤鱼钳事租111mem", "yes"))
//        return list
    }

    override fun getDataInternet(request: String,requestCode:Int,appExecutors: AppExecutors,callback: DataListSource.getDataCallBack): ByteArray {
        Threads.start(request,requestCode,appExecutors,callback)
        return Threads.byteArray!!
//        Thread.sleep(1000)
//        var list:MutableList<MainDataInfo> = ArrayList()
//        list.add(MainDataInfo("xiaoxiaoxa","yes"))
//        list.add(MainDataInfo("鲤鱼钳事租","yes"))
//        return list
    }

    override fun deleteData(boolean: Boolean) {

    }

}