package com.example.uav_client.Data.Common

class User {
    internal var id:Int = 0
    internal var name: String = ""
    internal var password: String = ""
    internal var areaNumber: Int = 0
    internal var type: String = SUPER_USER

    constructor(name1:String,password1:String,type1:String,areaNumber1:Int,id1:Int){
        id = id1
        name = name1
        password = password1
        areaNumber = areaNumber1
        type = type1
    }

    companion object {
        @JvmStatic
        val SUPER_USER = "super"
        @JvmStatic
        val NORMAL_USER = "normal"
    }
}