package com.example.uav_client

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uav_client.Adapter.AlarmAdapter
import com.example.uav_client.Contracts.MainTaskDetailContract
import com.example.uav_client.Data.Common.RequestBuildUtil
import com.example.uav_client.Prensenters.MainPresenter
import com.example.uav_client.View.SearchView

class AlarmsActivity : AppCompatActivity(),MainTaskDetailContract.View {
    lateinit var backIcon: ImageView
    lateinit var searchView:SearchView
    lateinit var recyclerView: RecyclerView
    lateinit var alarmAdapter: AlarmAdapter
    lateinit var presenter:MainTaskDetailContract.Presenter

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
        searchView.visibility = View.GONE
        Log.d("dataAlarm", String(dataList))
    }

    private fun initData() {
        presenter = MainPresenter(this)
        presenter.getData("",RequestBuildUtil.SEARCH_ALARM)
    }

    private fun initView() {
        backIcon = findViewById(R.id.alarm_back)
        recyclerView = findViewById(R.id.alarm_list)
        searchView = findViewById(R.id.load_view)
        alarmAdapter = AlarmAdapter()
        recyclerView.adapter = alarmAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        backIcon.setOnClickListener {
            this.finish()
        }
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
