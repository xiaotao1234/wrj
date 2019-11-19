package com.example.uav_client.Contracts

import com.example.uav_client.Base.BasePresenter
import com.example.uav_client.Base.BaseView
import com.example.uav_client.Data.Main.MainDataInfo

interface LoginContract {

    interface View : BaseView {
        fun loginFiled()
        fun loginsuccess()
    }


    interface Presenter : BasePresenter {
        fun login(name:String,password:String)
    }
}