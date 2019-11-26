package com.example.uav_client.Data.Common

import com.amap.api.maps.model.LatLng

class ReceiveBody {
    companion object {
        @JvmStatic
        fun initialParse(s: String,flagS:String): MutableList<String> { //将长字符串分开，存放到数组中
            var list: MutableList<String> = ArrayList()
            var index = 0
            var flag: Int
            while (s.indexOf(flagS, index, true)!=-1) {
                flag = s.indexOf(flagS, index, true)
                list.add(s.substring(index, flag).trim())
                flag += 1
                index = flag
            }
            list.add(s.substring(index,s.length))
            return list
        }

        @JvmStatic
        fun getAlarm(s:List<String>):List<LatLng>{
            var latLngsAlarm:MutableList<LatLng> = ArrayList()
            for(item in s){
                var s = initialParse(item,"|")
                latLngsAlarm.add(LatLng(s[0].toDouble(),s[1].toDouble()))
            }
            latLngsAlarm.add(latLngsAlarm[0])
            return latLngsAlarm
        }

        @JvmStatic
        fun getHead(s:String,flags1:String,flags2:String): MutableList<String> {
            var list = initialParse(s,flags1)
            var l:MutableList<String> = ArrayList()
            for(m in list){
                l.add(getFirst(m,flags2))
            }
            return l
        }

        @JvmStatic
        fun getFirst(s:String,flags2:String): String {
            return s.substring(0,s.indexOf(flags2,0)-1)
        }
    }
}