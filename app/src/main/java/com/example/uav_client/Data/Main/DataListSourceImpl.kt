package com.example.uav_client.Data.Main
import com.example.uav_client.Data.DataSS.dataSchdule
import com.example.uav_client.Network.Consumer
import com.example.uav_client.Utils.AppExecutors


class DataListSourceImpl(var appExecutors: AppExecutors,var dataschdule: dataSchdule<MainDataInfo>) : DataListSource{

    override fun getData(getDataCallBack: DataListSource.getDataCallBack,request:String,requestCode:Int) {
        val runnable = Runnable {
            Consumer(requestCode,getDataCallBack,appExecutors)
            val datalist:ByteArray = dataschdule.getDataInternet(request,requestCode,appExecutors,getDataCallBack)
//            appExecutors.mainThread().execute { getDataCallBack.dataGet(datalist) }
        }
        appExecutors.networkIO().execute(runnable)
    }

    override fun getDataMem(getDataCallBack: DataListSource.getDataCallBack) {
        val runnable = Runnable {
            val datalist:ByteArray = dataschdule.getDataMeM()
            appExecutors.mainThread().execute { getDataCallBack.dataGet(datalist) }
        }
        appExecutors.networkIO().execute(runnable)
    }

    override fun deleteData(deleteCallBack: DataListSource.deleteCallBack) {

    }

    companion object {
        @Volatile
        private var INSTANCE: DataListSourceImpl? = null

        fun getInstance(appExecutors: AppExecutors, dataschdule: dataSchdule<MainDataInfo>): DataListSourceImpl? {
            if (INSTANCE == null) {
                synchronized(DataListSourceImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = DataListSourceImpl(appExecutors, dataschdule)
                    }
                }
            }
            return INSTANCE
        }
    }
}