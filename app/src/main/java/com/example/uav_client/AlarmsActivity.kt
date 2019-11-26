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
import com.example.uav_client.Contracts.MainTaskDetailContract
import com.example.uav_client.Data.Common.ReceiveBody
import com.example.uav_client.Data.Common.RequestBuildUtil
import com.example.uav_client.Prensenters.MainPresenter
import com.example.uav_client.View.CalendarView
import com.example.uav_client.View.refreshV

class AlarmsActivity : AppCompatActivity(), MainTaskDetailContract.View {
    lateinit var backIcon: ImageView
    lateinit var recyclerView: RecyclerView
    lateinit var alarmAdapter: AlarmAdapter
    lateinit var presenter: MainTaskDetailContract.Presenter
    lateinit var refreshV: refreshV
    lateinit var calendarView:CalendarView
    var list: MutableList<MutableList<String>> = ArrayList()
    private var m: MutableList<Int> = ArrayList()
    var month:Int = 0
    var year:Int = 0
    var day:MutableList<Int> = ArrayList()

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarms)
        initView()
        hideHeader()
        initData()
    }

    override fun error() {

    }

    override fun release() {

    }

    override fun showList(dataList: ByteArray, requestCode: Int) {
        val s = String(dataList)
        Log.d("dataAlarm", String(dataList))
        endAnimtions()
        var lis = ReceiveBody.initialParse(s, ";")
        Log.d("listsize", lis.size.toString())
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

    private fun initData() {
        presenter = MainPresenter(this)
        presenter.getData("", RequestBuildUtil.SEARCH_ALARM)
    }
    private fun initView() {
        calendarView = findViewById(R.id.month_view)
        calendarView.setClickListener { addViewClick() }
        refreshV = findViewById(R.id.ref_layout)
        backIcon = findViewById(R.id.alarm_back)
        recyclerView = findViewById(R.id.logList)
        alarmAdapter = AlarmAdapter()
        recyclerView.adapter = alarmAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        backIcon.setOnClickListener {
            this.finish()
        }
        refreshV.setListener(object : refreshV.HeadListener {
            override fun callback() {
                Log.d("refreshtouchListener", "come")
                if (!refreshV.searchView.isRunning) {
                    var dp_50: Float = resources.getDimension(R.dimen.dp_50)
                    recyclerView.translationY = dp_50
//                    presenter.getData(RequestBuildUtil.transformRequestToByte())
                    refreshV.searchView.start()
                    presenter = MainPresenter(this@AlarmsActivity)
                    presenter.getData("", 13)
                    initData()
                }
            }
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    alarmAdapter.setlist(list)
                    alarmAdapter.notifyDataSetChanged()
                }
            }
        }
    }
    private fun addViewClick() {
        month = calendarView.month
        year = calendarView.year
        m = calendarView.getCheckPosition() as MutableList<Int>//多少号
        if (m.size == 1) {
            val position = m[0]
            if (position != -1) {
                day.add(m[0])
            }
        } else if (m.size == 2) {
            day.add(m[0])
            day.add(m[1])
        }
        Log.d("month", month.toString())
        Log.d("year", year.toString())
        Log.d("day", m.toString())
    }

    fun endAnimtions() {
        refreshV.searchView.end()
        recyclerView.translationY = 0F
    }


    private fun hideHeader() {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.parseColor("#FFFFFF")
        }
    }
}
