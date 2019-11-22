package com.example.uav_client.Data.Common

class NobodyAirplaneData {
    var id:String = "" //无人机ID
    var frequency:Long = 0//频率
    var lat:Double = 0.0//经度
    var lon:Double = 0.0//纬度
    var aop:Double = 0.0//俯仰角
    var data: String = ""//日期时间
    var isAlarm:Int = 0//是否报警

    constructor(id: String, frequency: Long, lat: Double, lon: Double, aop: Double, data: String, isAlarm: Int) {
        this.id = id
        this.frequency = frequency
        this.lat = lat
        this.lon = lon
        this.aop = aop
        this.data = data
        this.isAlarm = isAlarm
    }
}