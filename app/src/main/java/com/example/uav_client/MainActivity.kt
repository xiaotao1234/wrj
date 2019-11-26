package com.example.uav_client

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.UiSettings
import com.amap.api.maps.model.*
import com.amap.api.maps.offlinemap.OfflineMapActivity
import com.example.uav_client.Adapter.MainPageAdapter
import com.example.uav_client.Application.BaseActivity
import com.example.uav_client.Application.SysApplication
import com.example.uav_client.Application.SysApplication.Companion.datahash
import com.example.uav_client.Contracts.MainTaskDetailContract
import com.example.uav_client.Data.Common.NobodyAirplaneData
import com.example.uav_client.Data.Common.ReceiveBody
import com.example.uav_client.Data.Common.RequestBuildUtil
import com.example.uav_client.Data.Common.User
import com.example.uav_client.Data.Main.DataListSource
import com.example.uav_client.Data.Main.MainDataInfo
import com.example.uav_client.Network.Consumer
import com.example.uav_client.Prensenters.MainPresenter
import com.example.uav_client.View.airplanePath
import com.example.uav_client.View.refreshV
import com.google.android.material.navigation.NavigationView
import kotlin.collections.ArrayList

class MainActivity : BaseActivity(), MainTaskDetailContract.View, AMap.OnMapClickListener {

    internal lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: MainPageAdapter
    internal lateinit var refreshV: refreshV
    private var aMap: AMap? = null
    lateinit var mUiSettings: UiSettings
    private lateinit var map: MapView
    private lateinit var editText: EditText
    private lateinit var usernameTop: TextView
    private lateinit var userRight: TextView
    internal var datalist: MutableList<MainDataInfo> = ArrayList()
    internal lateinit var presenter: MainPresenter
    private lateinit var searchLay: FrameLayout
    private lateinit var search: ImageView
    private lateinit var clearButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menu: ImageView
    private lateinit var navigation: NavigationView
    private var flagStatus: Boolean = true
    private lateinit var airplaypath: airplanePath
    private var time: Long = 0
    private var markerOption: MarkerOptions? = null
    private lateinit var marker: Marker
    var polygon: Polygon? = null
    private val latLngs: ArrayList<LatLng>
        get() {
            val latLngs = ArrayList<LatLng>()
            return latLngs
        }

    override fun showList(dataList: ByteArray, requestCode: Int) {
        this.datalist.clear()
        var s = String(dataList)
        var lis = ReceiveBody.initialParse(s,"#")
        for(l in lis){
            var list1 = ReceiveBody.initialParse(l,"!")
            datalist.add(MainDataInfo(list1[0],list1[1],list1[2]))
            datahash.add(list1[3])
        }
        listAdapter = MainPageAdapter(datalist, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = listAdapter
        engAnimtions()

        Log.d("history", String(dataList))
        Toast.makeText(this, "数据刷新成功", Toast.LENGTH_SHORT).show()
    }

    private fun engAnimtions() {
        refreshV.searchView.end()
        recyclerView.translationY = 0F
    }

    override fun release() {

    }


    override fun error() {
        Toast.makeText(this, "网络异常", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)
        hideHeader()
        initView(savedInstanceState)
//        dataInit()
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

//    private fun dataInit() {
////        presenter = MainPresenter(this)
//        var dp_50: Float = resources.getDimension(R.dimen.dp_50)
//        recyclerView.translationY = dp_50
////        presenter.getData()
//        refreshV.searchView.start()
////        presenter.getData("",13)
//    }


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
        airplaypath!!.flagstop = true
        Consumer.addMainObserver(null)
        super.onPause()
    }

    override fun onResume() {
        airplaypath!!.flagstop = false
        super.onResume()
//        refreshV.postDelayed({ dataInit() },5000)
        dataInit()
        addObserver()
    }

    private fun addObserver() {
        Consumer.addMainObserver(object : DataListSource.getDataCallBack {  //在线
            override fun dataGet(dataList: ByteArray) {
                if (dataList.size == 0) {
                    drawArea(SysApplication.alarmArea)
                } else {
                    var dataList1 = RequestBuildUtil.unPack(dataList)
                    var s = String(dataList1)
                    var ss = ReceiveBody.initialParse(s, "|")
                    drawOnMap(NobodyAirplaneData(ss[0], ss[1].toLong(), ss[2].toDouble(), ss[3].toDouble(), ss[4].toDouble(), ss[5], ss[6].toInt()))
                }
            }
            override fun error() {
            }
        })
    }

    fun drawOnMap(nobodyAirplaneData: NobodyAirplaneData) {
        changeCamera(LatLng(nobodyAirplaneData.lat, nobodyAirplaneData.lon), true)
        addMarkersToMap(LatLng(nobodyAirplaneData.lat, nobodyAirplaneData.lon))
    }

    private fun changeCamera(latLng: LatLng, room: Boolean) { //改变地图坐标位置
        var zoom = aMap!!.getCameraPosition().zoom
        if (room) {
            aMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(
                    CameraPosition.builder()
                            .target(latLng)
                            .tilt(18f)//目标区域倾斜度
                            .zoom(17f)//缩放级别
                            .bearing(30f)//旋转角度
                            .build()))
        } else {
            aMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(
                    CameraPosition.builder()
                            .target(latLng)
                            .tilt(18f)//目标区域倾斜度
                            .zoom(zoom)//缩放级别
                            .bearing(30f)//旋转角度
                            .build()))
        }
    }

    private fun addMarkersToMap(latLng: LatLng) {  //定点化标志
        latLngs.add(latLng)
        markerOption = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.point))
                .position(latLng)
                .draggable(true)
        marker = aMap!!.addMarker(markerOption)
    }

    fun drawArea(latLngs: List<LatLng>) {
//        if (latLngs.size > 3) {
//            polygon.remove()
//        }
//                addMarkersToMap(latLngs[1])
        if (polygon != null) {
            polygon!!.remove()
        }
        val polygonOptions1 = PolygonOptions()
                .fillColor(Color.parseColor("#11000000")).strokeColor(Color.RED).strokeWidth(10f)
        polygonOptions1.addAll(latLngs)
        polygon = aMap!!.addPolygon(polygonOptions1)
        changeCamera(latLngs[latLngs.lastIndex], false)
    }

    private fun dataInit() {
        presenter = MainPresenter(this)
        presenter.getData("", RequestBuildUtil.SEARCH_UAV)
    }

//    private fun initData() {
//        presenter = MainPresenter(this)
//        presenter!!.getData("", RequestBuildUtil.SEARCH_USER_LIST)
//    }

    override fun onMapClick(p0: LatLng?) {
        startActivity(Intent(this@MainActivity, MapActivity::class.java))
    }

    private fun initView(savedInstanceState: Bundle?) {
        map = findViewById(R.id.map)
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
        map.onCreate(savedInstanceState)
        if (aMap == null) {
            aMap = map.map
            aMap!!.setOnMapClickListener(this)
            mUiSettings = aMap!!.uiSettings
            mUiSettings.isScrollGesturesEnabled = false
        }
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
            dataInit()
            searchChange()
        }
        searchLay.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                hideEdit()
            }
        })
        refreshV.setListener(object : refreshV.HeadListener {
            override fun callback() {
                Log.d("refreshtouchListener","come")
                if (!refreshV.searchView.isRunning) {
                    var dp_50: Float = resources.getDimension(R.dimen.dp_50)
                    recyclerView.translationY = dp_50
//                    presenter.getData(RequestBuildUtil.transformRequestToByte())
                    refreshV.searchView.start()
                    presenter = MainPresenter(this@MainActivity)
                    presenter.getData("", 13)
                    dataInit()
                }
            }
        })
        engAnimtions()
        refreshV.searchView.start()
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
