package com.example.uav_client.Data.Common

class User {
    internal var id:String = ""
    internal var name: String = ""
    internal var password: String = ""
    internal var areaNumber: Int = 0
    internal var type: String = SUPER_USER

    constructor(id1:String,name1:String,password1:String,areaNumber1:Int,type1:String){
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
        val NORMAL_USER = "nomral"
    }

    override fun equals(other: Any?): Boolean {
        if(other is User){
            if(id == other.id){
                return true
            }
        }
        return false
    }
}