package com.example.uav_client.Data.Main


interface DataListSource {
    interface getDataCallBack{
        fun dataGet(dataList:ByteArray)
        fun error()
    }

    interface deleteCallBack{
        fun deleteData(isDelete:Boolean)
    }

    fun getData(getDataCallBack: getDataCallBack,request:String,requestCode:Int)

    fun getDataMem(getDataCallBack: getDataCallBack)

    fun deleteData(deleteCallBack: deleteCallBack)
}