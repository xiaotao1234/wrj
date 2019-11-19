package com.example.uav_client.Contracts

import com.example.uav_client.Base.BasePresenter
import com.example.uav_client.Base.BaseView
import com.example.uav_client.Data.Main.MainDataInfo

interface MainTaskDetailContract {

    interface View : BaseView {
        fun showList(dataList: ByteArray)
        fun error()
    }

    interface Presenter : BasePresenter {
        fun getData(request:String,requestCode:Int)
        fun release()
    }
}