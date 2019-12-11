package com.example.uav_client

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
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
import com.example.uav_client.Data.Common.*
import com.example.uav_client.Data.Main.DataListSource
import com.example.uav_client.Data.Main.MainDataInfo
import com.example.uav_client.Network.Consumer
import com.example.uav_client.Network.Threads
import com.example.uav_client.Prensenters.MainPresenter
import com.example.uav_client.View.airplanePath
import com.example.uav_client.View.refreshV
import com.google.android.material.navigation.NavigationView
import java.util.*
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
    var searchList: MutableList<MainDataInfo> = ArrayList()
    private val stationList: MutableList<Station> = ArrayList()
    private val stationMarkerList: MutableList<Marker> = ArrayList()
    private var markerOptionStation: MarkerOptions? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menu: ImageView
    private lateinit var navigation: NavigationView
    private var flagStatus: Boolean = true
    private lateinit var airplaypath: airplanePath
    private var time: Long = 0
    private var markerOption: MarkerOptions? = null
    private lateinit var marker: Marker
    var markerList: MutableList<Marker> = LinkedList()
    var polygon: Polygon? = null
    lateinit var handler: Handler
    val latLngs: ArrayList<LatLng>
        get() {
            return ArrayList()
        }

    override fun showList(dataList: ByteArray, requestCode: Int) {
        this.datalist.clear()
        var s = String(dataList)
        var lis = ReceiveBody.initialParse(s, "#")
        for (l in lis) {
            var list1 = ReceiveBody.initialParse(l, "!")
            datalist.add(MainDataInfo(list1[0], list1[1], list1[2]))
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


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        if (flagStatus) {
            searchChange()
        } else {
            if (time.toInt() == 0) {
                time = System.currentTimeMillis()
                Toast.makeText(this, "再次点击退出应用", Toast.LENGTH_SHORT).show()
            } else {
                if (System.currentTimeMillis() - time < 2000) {
                    Threads.exit()
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
        dataInit()
        addObserver()
        addOStationbserver()
        changeAtBegin()
        if (SysApplication.alarmArea.size > 0) {
            drawerLayout.postDelayed({ drawArea(SysApplication.alarmArea) }, 1000)
        }
        if (SysApplication.stationItem.size > 0) {
            drawerLayout.postDelayed({ drawStation() }, 1000)
        }
    }

    private fun addOStationbserver() {
        Consumer.addObserverMapStation(object : DataListSource.getDataCallBack {
            override fun dataGet(dataList: ByteArray) {
                if (dataList.isEmpty()) {
                    drawStation()
                }
            }

            override fun error() {

            }
        })
    }

    private fun drawStation() {
        for(item in stationMarkerList){
            item.remove()
        }
        stationList.clear()
        stationMarkerList.clear()
        var lon = 0.0
        var lat = 0.0
        for(item in SysApplication.stationItem){
            var list = ReceiveBody.initialParse(item,"|")
            stationList.add(Station(list[0],list[1],list[2],list[3].toDouble(),list[4].toDouble()))
            when(list[2]){
                "正常" -> markerOptionStation = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.station_unuse))
                        .position(LatLng(list[4].toDouble(),list[3].toDouble()))
                        .draggable(true)
                "故障" -> markerOptionStation = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.station_icon_trouble))
                        .position(LatLng(list[4].toDouble(),list[3].toDouble()))
                        .draggable(true)
                "正在使用" -> markerOptionStation = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.station_use))
                        .position(LatLng(list[4].toDouble(),list[3].toDouble()))
                        .draggable(true)
            }
            marker = aMap!!.addMarker(markerOptionStation)
            stationMarkerList.add(marker)
            lon = list[4].toDouble()
            lat = list[3].toDouble()
        }
        Toast.makeText(this,"站点数据成功加载到地图",Toast.LENGTH_SHORT).show()
        changeCamera(LatLng(lon,lat),true)
    }

    private fun changeAtBegin() {
        var sharedPreferences = this.getSharedPreferences("latlng", Context.MODE_PRIVATE)
        var lon = sharedPreferences.getFloat("lon", 0.0F)
        var lat = sharedPreferences.getFloat("lat", 0.0F)
        if (!lon.equals(0.0F)) {
            refreshV.postDelayed({
                changeCamera(LatLng(lon.toDouble(), lat.toDouble()), true)
            }, 500)
        }
    }

    private fun addObserver() {
        Consumer.addMainObserver(object : DataListSource.getDataCallBack {  //在线
            override fun dataGet(dataList: ByteArray) {
                if (dataList.isEmpty()) {
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
        var zoom = aMap!!.cameraPosition.zoom
        if (room) {
            aMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(
                    CameraPosition.builder()
                            .target(latLng)
                            .tilt(18f)//目标区域倾斜度
                            .zoom(18f)//缩放级别
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

    private fun addMarkersToMap(latLng: LatLng) {  //定点画标志

        if (latLngs.size > 0) {
            marker.remove()
            markerOption = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.point))
                    .position(latLngs[latLngs.size - 1])
                    .draggable(false)
            aMap!!.addMarker(markerOption)
        }

        markerOption = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.airplane_small_map))
                .position(latLng)
                .draggable(false)
        marker = aMap!!.addMarker(markerOption)
        latLngs.add(latLng)
    }

    fun drawArea(latLngs: List<LatLng>) {
        if (polygon != null) {
            polygon!!.remove()
        }
        val polygonOptions1 = PolygonOptions()
                .fillColor(Color.parseColor("#11000000")).strokeColor(Color.RED).strokeWidth(4f)
        polygonOptions1.addAll(latLngs)
        polygon = aMap!!.addPolygon(polygonOptions1)
        changeCamera(latLngs[latLngs.lastIndex], true)
        Toast.makeText(this,"报警区域信息成功加载到地图",Toast.LENGTH_SHORT).show()
    }

    private fun dataInit() {
        presenter = MainPresenter(this)
        map.postDelayed({presenter.getData("", RequestBuildUtil.SEARCH_UAV)},1000)
    }

    override fun onMapClick(p0: LatLng?) {

        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra("id", -1)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
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
            mUiSettings.isZoomControlsEnabled = false
            mUiSettings.isZoomGesturesEnabled = false
            mUiSettings.isScrollGesturesEnabled = false
            mUiSettings.isRotateGesturesEnabled = false
            mUiSettings.isTiltGesturesEnabled = false
            mUiSettings.setAllGesturesEnabled(false)
            mUiSettings.isScaleControlsEnabled = true
        }
        editText.setOnClickListener {
            showSoftInputFromWindow(editText)
        }
        editText!!.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchInList()
                true
            }
            false
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var thread = Thread(Runnable {
                    searchList.clear()
                    for (s1 in datalist) {
                        if (s1.id.contains(s.toString(), true)) {
                            searchList.add(s1)
                        }
                    }
                    handler.sendEmptyMessage(1)
                })
                thread.start()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        navigation.setNavigationItemSelectedListener { p0 ->
            when (p0.itemId) {
                R.id.user_manager -> {
                    if (SysApplication.user.type == User.SUPER_USER) {
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
        usernameTop.text = "用户名：" + SysApplication.user.name
        userRight.text = "用户权限：" + if (SysApplication.user.type == User.SUPER_USER) "管理员" else "普通用户"
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
                Log.d("refreshtouchListener", "come")
                if (!refreshV.searchView.isRunning) {
                    var dp_50: Float = resources.getDimension(R.dimen.dp_50)
                    recyclerView.translationY = dp_50
                    refreshV.searchView.start()
                    presenter = MainPresenter(this@MainActivity)
                    presenter.getData("", 13)
                    dataInit()
                }
            }
        })
        engAnimtions()
        refreshV.searchView.start()
        handler = object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                if (msg!!.what == 1) {
                    listAdapter.datalist = searchList
                    listAdapter.notifyDataSetChanged()
                }
            }
        }
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

    private fun searchInList() {
        Toast.makeText(this, "search", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        if (presenter != null) {
            false
        }
        super.onDestroy()
    }
}
