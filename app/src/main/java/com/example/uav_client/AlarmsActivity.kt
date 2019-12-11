package com.example.uav_client

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uav_client.Adapter.AlarmAdapter
import com.example.uav_client.Contracts.MainTaskDetailContract
import com.example.uav_client.Data.Common.ReceiveBody
import com.example.uav_client.Data.Common.RequestBuildUtil
import com.example.uav_client.Prensenters.MainPresenter
import com.example.uav_client.View.CalendarView
import com.example.uav_client.View.refreshV
import org.angmarch.views.NiceSpinner

class AlarmsActivity : AppCompatActivity(), MainTaskDetailContract.View {
    lateinit var backIcon: ImageView
    lateinit var recyclerView: RecyclerView
    lateinit var alarmAdapter: AlarmAdapter
    lateinit var presenter: MainTaskDetailContract.Presenter
    lateinit var refreshV: refreshV
    lateinit var calendarView: CalendarView
    var list: MutableList<MutableList<String>> = ArrayList()
    private var m: IntArray = IntArray(2)
    private var uavlist: MutableList<MutableList<String>> = ArrayList()
    private var listcz: MutableList<MutableList<String>> = ArrayList()
    private var listcanlder: MutableList<MutableList<String>> = ArrayList()
    var dataClocect: MutableList<String> = ArrayList()
    var dataClocect1: MutableList<String> = ArrayList()
    var listcollect: MutableList<MutableList<String>> = ArrayList()
    var month: Int = 0
    var year: Int = 0
    var day: MutableList<Int> = ArrayList()
    var users: MutableList<String> = ArrayList()

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

    private fun setSpinner1(s: MutableList<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, s)  //创建一个数组适配器
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)     //设置下拉列表框的下拉选项样式
        val spinner = super.findViewById<NiceSpinner>(R.id.spinner1)
        spinner.attachDataSource(s)

        spinner!!.addOnItemClickListener { parent, view, position, id ->
            uavlist.clear()
            var s1 = s[position]
            for (s in list) {
                if (s[1].equals(s1)) {
                    uavlist.add(s)
                }
            }
            alarmAdapter.setlist(uavlist)
            alarmAdapter.notifyDataSetChanged()
        }
    }

    private fun setSpinner2(s: MutableList<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, s)  //创建一个数组适配器
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)     //设置下拉列表框的下拉选项样式
        val spinner = super.findViewById<NiceSpinner>(R.id.spinner2)
        spinner.attachDataSource(s)
        spinner!!.addOnItemClickListener { _, _, position, _ ->
            var s1 = s[position]
            for (s in list) {
                if (s[2] == s1) {
                    listcz.add(s)
                }
            }
            alarmAdapter.setlist(listcz)
            alarmAdapter.notifyDataSetChanged()
        }
    }

    override fun showList(dataList: ByteArray, requestCode: Int) {
        val s = String(dataList)
        Log.d("dataAlarm", String(dataList))
        endAnimtions()
        if(s!="0"){
            var lis = ReceiveBody.initialParse(s, ";")
            Log.d("listsize", lis.size.toString())
            var thread = Thread(
                    Runnable {
                        var lis = ReceiveBody.initialParse(s, ";")
                        for (s in lis) {
                            list.add(ReceiveBody.initialParse(s, "|"))
                            if (users.size == 0) {
                                users.add(list[0][1])
                            } else if (!users.contains(list[list.size - 1][1])) {
                                users.add(list[list.size - 1][1])
                            }
                        }
                        handler.sendEmptyMessage(1)
                    }
            )
            thread.start()
        }else{
            Toast.makeText(this,"暂无数据",Toast.LENGTH_SHORT).show()
        }
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
//                Log.d("refreshtouchListener", "come")
//                if (!refreshV.searchView.isRunning) {
//                    var dp_50: Float = resources.getDimension(R.dimen.dp_50)
//                    recyclerView.translationY = dp_50
////                    presenter.getData(RequestBuildUtil.transformRequestToByte())
//                    refreshV.searchView.start()
//                    presenter = MainPresenter(this@AlarmsActivity)
//                    presenter.getData("", 13)
//                    initData()
//                }
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

                    setSpinner1(users)
                } else if (msg!!.what == 2) {
                    alarmAdapter.setlist(listcollect)
                    alarmAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun addViewClick() {
        calendarView.postDelayed({ getcanler() }, 200)
    }

    private fun getcanler() {
        month = calendarView.month
        year = calendarView.year
        m = calendarView.checkPosition as IntArray//多少号
        if (m.size == 1) {
            val position = m[0]
            if (position != -1) {
                day.clear()
                day.add(m[0])
            }
        } else if (m.size == 2) {
            day.clear()
            day.add(m[0])
            day.add(m[1])
        }
        Log.d("month", month.toString())
        Log.d("year", year.toString())
        Log.d("day", m.toString())
        if (day.size == 1) {
            var ss = String.format("%02d", m[0])
            var sr = m[0]
            var s1 = "$year-$month-$sr"
            var s2 = "$year/$month/$sr"
            listcollect.clear()
            for (s in list) {
                var sm = s[0].substringBefore(" "," ")
                if (s1 == sm || s2 == sm) {
                    listcollect.add(s)
                }
            }
            handler.sendEmptyMessage(2)
        } else if (day.size == 2){
            var start = if(day[0]>day[1]) day[1] else day[0]
            var end = if(day[0]<day[1]) day[1] else day[0]
            dataClocect.clear()
            dataClocect1.clear()
            listcollect.clear()
            loop@ for (i in start..end) {
                var ss = String.format("%02d", i)
                dataClocect.add("$year-$month-$i")
                dataClocect1.add("$year/$month/$i")
            }
            for (s in list) {
                var m = s[0].substringBefore(" "," ")
                if (dataClocect.contains(m)||dataClocect1.contains(m)) {
                    listcollect.add(s)
                }
            }
            handler.sendEmptyMessage(2)
        }
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
