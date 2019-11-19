package com.example.uav_client.Data.DataSS

import com.example.uav_client.Data.Main.DataListSource
import com.example.uav_client.Utils.AppExecutors


interface dataSchdule<T> {
    fun getDataInternet(request:String,requestCode:Int,appExecutors: AppExecutors,callback: DataListSource.getDataCallBack): ByteArray//从网络进行数据加载
    fun deleteData(boolean:Boolean)
    fun getDataMeM():ByteArray //从内存进行数据加载
}