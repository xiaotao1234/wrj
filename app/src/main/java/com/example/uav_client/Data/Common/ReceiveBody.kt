package com.example.uav_client.Data.Common

class ReceiveBody {
    fun initialParse(s:String){
        var list: MutableList<String> = ArrayList()
        var index:Int
        var flag = 0
        while (flag!=-1){
            index = flag
            flag = s.indexOf(";",index,true)
            list.add(s.substring(index,flag))
        }

    }
}