package com.example.uav_client

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uav_client.Adapter.AlarmAdapter
import com.example.uav_client.Adapter.LogAdapter
import com.example.uav_client.Contracts.MainTaskDetailContract
import com.example.uav_client.Data.Common.ReceiveBody
import com.example.uav_client.Data.Common.RequestBuildUtil
import com.example.uav_client.Prensenters.MainPresenter
import com.example.uav_client.View.refreshV

class LogActivity : AppCompatActivity(), MainTaskDetailContract.View {
    lateinit var backIcon: ImageView
    lateinit var presenter: MainPresenter
    lateinit var recycleview: RecyclerView
    lateinit var refreshV: refreshV
    lateinit var logadapter: LogAdapter
    lateinit var handler: Handler
    var list: MutableList<MutableList<String>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        hideHeader()
        initView()
        initData()
    }

    private fun initData() {
        presenter = MainPresenter(this)
        presenter.getData("51", RequestBuildUtil.SEARCH_DAILY)
    }

    private fun initView() {
        logadapter = LogAdapter()
        recycleview = findViewById(R.id.logList)
        backIcon = findViewById(R.id.alarm_back)
        refreshV = findViewById(R.id.ref_layout)
        recycleview.adapter = logadapter
        recycleview.layoutManager = LinearLayoutManager(this)
        backIcon.setOnClickListener {
            this.finish()
        }

        refreshV.setListener(object : refreshV.HeadListener {
            override fun callback() {
                Log.d("refreshtouchListener", "come")
                if (!refreshV.searchView.isRunning) {
                    var dp_50: Float = resources.getDimension(R.dimen.dp_50)
                    recycleview.translationY = dp_50
//                    presenter.getData(RequestBuildUtil.transformRequestToByte())
                    refreshV.searchView.start()
                    presenter = MainPresenter(this@LogActivity)
                    presenter.getData("", 13)
                    initData()
                }
            }
        })

        recycleview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                var layoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                var firstCompletelyVisibleItemPosition: Int = layoutManager.findFirstCompletelyVisibleItemPosition()
                refreshV.top = firstCompletelyVisibleItemPosition == 0
                val lastCompletelyVisibleItemPosition: Int = layoutManager.findLastCompletelyVisibleItemPosition()
                if (lastCompletelyVisibleItemPosition == layoutManager.getItemCount() - 1) {

                }
            }
        })
        handler = object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                if (msg!!.what == 1) {
                    logadapter.setlist(list)
                    logadapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshV.searchView.start()
    }

    fun engAnimtions() {
        refreshV.searchView.end()
        recycleview.translationY = 0F
    }

    private fun hideHeader() {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.parseColor("#FFFFFF")
        }
    }

    override fun showList(dataList: ByteArray, requestCode: Int) {
        Log.d("dataLog", String(dataList))
        var s = String(dataList)
        engAnimtions()
        var thread = Thread(
                Runnable {
                    var lis = ReceiveBody.initialParse(s, ";")
                    for (s in lis) {
                        list.add(ReceiveBody.initialParse(s, "|"))
                    }
                    handler.sendEmptyMessage(1)
                }
        )
        thread.start()
    }

    override fun error() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun release() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
