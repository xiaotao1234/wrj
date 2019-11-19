package com.example.uav_client

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.maps.offlinemap.OfflineMapActivity
import com.example.uav_client.*
import com.example.uav_client.Adapter.MainPageAdapter
import com.example.uav_client.Application.SysApplication
import com.example.uav_client.Contracts.MainTaskDetailContract
import com.example.uav_client.Data.Common.User
import com.example.uav_client.Data.Main.MainDataInfo
import com.example.uav_client.Prensenters.MainPresenter
import com.example.uav_client.View.airplanePath
import com.example.uav_client.View.refreshV
import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity : AppCompatActivity(), MainTaskDetailContract.View {
    internal lateinit var recyclerView: RecyclerView
    internal lateinit var listAdapter: MainPageAdapter
    internal lateinit var refreshV: refreshV
    internal lateinit var editText: EditText
    internal lateinit var usernameTop: TextView
    internal lateinit var userRight: TextView
    internal var datalist: MutableList<MainDataInfo> = ArrayList()
    internal lateinit var presenter: MainTaskDetailContract.Presenter
    internal lateinit var searchLay: FrameLayout
    internal lateinit var search: ImageView
    internal lateinit var clearButton: ImageView
    internal lateinit var drawerLayout: DrawerLayout
    internal lateinit var menu: ImageView
    internal lateinit var navigation: NavigationView
    internal var flagStatus: Boolean = true
    internal lateinit var airplaypath: airplanePath
    internal var time: Long = 0

    override fun showList(dataList: ByteArray) {
//        this.datalist = dataList as MutableList<MainDataInfo>
//        listAdapter = MainPageAdapter(datalist,this)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = listAdapter
//        refreshV.searchView.end()
//        recyclerView.translationY = 0F
        Toast.makeText(this, "数据刷新成功", Toast.LENGTH_SHORT).show()
    }

    override fun release() {
    }


    override fun error() {
        Toast.makeText(this,"网络异常",Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)
        hideHeader()
        initView()
        dataInit()
        dataIntoView()
    }

    private fun hideHeader() {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.parseColor("#FFFFFF")
        }
    }


    private fun dataIntoView() {
        listAdapter = MainPageAdapter(datalist, this)
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
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = listAdapter
    }

    private fun dataInit() {
        presenter = MainPresenter(this)
        var dp_50: Float = resources.getDimension(R.dimen.dp_50)
        recyclerView.translationY = dp_50
//        presenter.getData()
        refreshV.searchView.start()
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        if (flagStatus) {
            searchChange()
        } else {
            if (time.toInt().equals(0)) {
                time = System.currentTimeMillis()
                Toast.makeText(this, "再次点击退出应用", Toast.LENGTH_SHORT).show()
            } else {
                if (System.currentTimeMillis() - time < 2000) {
                    super.onBackPressed()
                } else {
                    time = System.currentTimeMillis()
                    Toast.makeText(this, "再次点击退出应用", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showSoftInputFromWindow(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        val inputManager = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(editText, 0)
    }

    override fun onPause() {
        super.onPause()
        airplaypath!!.flagstop = true
    }

    override fun onResume() {
        airplaypath!!.flagstop = false
        super.onResume()
    }

    private fun initView() {
        refreshV = findViewById(R.id.ref_layout)
        refreshV.searchView.start()
        editText = findViewById(R.id.main_edit)
        recyclerView = findViewById(R.id.data_list)
        searchLay = findViewById(R.id.search_lay)
        search = findViewById(R.id.search)
        clearButton = findViewById(R.id.clear_button)
        drawerLayout = findViewById(R.id.drawer_layout)
        menu = findViewById(R.id.menu)
        navigation = findViewById(R.id.nav_view)
        editText.setOnClickListener {
            showSoftInputFromWindow(editText)
        }
        editText!!.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                var imm:InputMethodManager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                searchInList(editText.text.toString())
                true
            }
            false
        }
        navigation.setNavigationItemSelectedListener { p0 ->
            when (p0.itemId) {
                R.id.user_manager -> {
                    if (SysApplication.user.type.equals(User.SUPER_USER)) {
                        startActivity(Intent(this@MainActivity, UserManagerActivity::class.java))
                    } else {
                        Toast.makeText(this, "普通用户没有进行用户管理的权限", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.map_download -> startActivity(Intent(this@MainActivity, OfflineMapActivity::class.java))
                R.id.alarm -> startActivity(Intent(this@MainActivity, AlarmsActivity::class.java))
                R.id.map_utils -> startActivity(Intent(this@MainActivity, MapUtilActivity::class.java))
                R.id.log_item -> startActivity(Intent(this@MainActivity, LogActivity::class.java))
            }
            true
        }
        var view = navigation.inflateHeaderView(R.layout.main_head)
        usernameTop = view.findViewById(R.id.user_name_show)
        userRight = view.findViewById(R.id.user_right)
        usernameTop.setText(SysApplication.user.name)
        userRight.setText("当前用户类型:" + if (SysApplication.user.type.equals(User.SUPER_USER)) "管理员" else "普通用户")
        airplaypath = view.findViewById(R.id.air_layout)
        var thread = Thread(airplaypath)
        view.postDelayed({ thread.start() }, 1000)
        menu.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        clearButton.setOnClickListener {
            if (editText.text.toString().isEmpty()) {
                searchChange()
            } else {
                editText.text = null
            }
        }
        search.setOnClickListener {
            searchChange()
        }
        searchLay.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                hideEdit()
            }
        })
        refreshV.setListener(object : refreshV.HeadListener {
            override fun callback() {
                if (!refreshV.searchView.isRunning) {
                    var dp_50: Float = resources.getDimension(R.dimen.dp_50)
                    recyclerView.translationY = dp_50
//                    presenter.getData(RequestBuildUtil.transformRequestToByte())
                    refreshV.searchView.start()
                }
            }
        })
    }

    private fun ViewTreeObserver.OnGlobalLayoutListener.hideEdit() {
        var searchWidth: Int = searchLay.width
        searchLay.translationX = searchWidth.toFloat()
        flagStatus = false
        searchLay.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }


    private fun searchChange() {
        if (flagStatus) {
            ObjectAnimator.ofFloat(searchLay, "translationX", searchLay.width.toFloat()).setDuration(500).start()
        } else {
            ObjectAnimator.ofFloat(searchLay, "translationX", 0F).setDuration(500).start()
        }
        flagStatus = !flagStatus
    }

    private fun searchInList(s: String) {
        Toast.makeText(this, "search", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        if (presenter != null) {
            false
        }
        super.onDestroy()
    }
}
