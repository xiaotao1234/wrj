package com.example.uav_client.Prensenters

import com.example.uav_client.Contracts.MainTaskDetailContract
import com.example.uav_client.Data.Common.RequestBuildUtil
import com.example.uav_client.Data.DataSS.dataSchduleImpl
import com.example.uav_client.Data.Main.DataListSource
import com.example.uav_client.Data.Main.DataListSourceImpl
import com.example.uav_client.Utils.AppExecutors

class MainPresenter : MainTaskDetailContract.Presenter {
    override fun release() {
        mainActivity
    }

    internal var mainActivity: MainTaskDetailContract.View
    private var datasource: DataListSourceImpl

    constructor(view: MainTaskDetailContract.View) {
        this.mainActivity = view
        this.datasource = DataListSourceImpl.getInstance(AppExecutors(), dataSchduleImpl())!!
    }

    override fun getData(request:String,requestCode:Int) {
        var firstIn = false
        if(firstIn){
            datasource!!.getDataMem(object :DataListSource.getDataCallBack{
                override fun dataGet(dataList: ByteArray) {
                    mainActivity ?.showList(dataList)
                }

                override fun error() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }
        datasource!!.getData(object : DataListSource.getDataCallBack {
            override fun dataGet(dataList: ByteArray) {
                mainActivity?.showList(RequestBuildUtil.unPack(dataList)!!)
            }

            override fun error() {
                mainActivity.error()
            }
        },request,requestCode)
    }

}