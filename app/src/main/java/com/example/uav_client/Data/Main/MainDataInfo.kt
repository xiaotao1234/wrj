package com.example.uav_client.Data.Main

class  MainDataInfo {
    internal var Starttime: String

    internal var endTiem: String

    internal var id:String

    constructor(id:String,Start: String, end: String) {
        this.id = id
        this.Starttime = Start
        this.endTiem = end
    }
}