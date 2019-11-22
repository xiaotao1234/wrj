package com.example.uav_client

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.example.uav_client.Contracts.MainTaskDetailContract
import com.example.uav_client.Data.Common.RequestBuildUtil
import com.example.uav_client.Prensenters.MainPresenter

class LogActivity : AppCompatActivity(),MainTaskDetailContract.View {
    lateinit var backIcon: ImageView
    lateinit var presenter:MainTaskDetailContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        hideHeader()
        initView()
        initData()
    }

    private fun initData() {
        presenter = MainPresenter(this)
        presenter.getData("",RequestBuildUtil.SEARCH_DAILY)
    }

    private fun initView() {
        backIcon = findViewById(R.id.alarm_back)
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

    override fun showList(dataList: ByteArray, requestCode: Int) {
        Log.d("dataLog", String(dataList))
    }

    override fun error() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun release() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
