package com.example.uav_client.Data.DataSS

import com.example.uav_client.Data.Main.DataListSource
import com.example.uav_client.Data.Main.MainDataInfo
import com.example.uav_client.Network.Threads
import com.example.uav_client.Utils.AppExecutors

class dataSchduleImpl : dataSchdule<MainDataInfo> {
    override fun getDataMeM(): ByteArray {
        return Threads.byteArray!!
    }

    override fun getDataInternet(request: String,requestCode:Int,appExecutors: AppExecutors,callback: DataListSource.getDataCallBack): ByteArray {
        Threads.start(request,requestCode,appExecutors,callback)
        return Threads.byteArray!!
    }

    override fun deleteData(boolean: Boolean) {

    }

}