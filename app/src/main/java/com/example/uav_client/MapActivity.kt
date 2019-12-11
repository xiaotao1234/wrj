package com.example.uav_client

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.maps.*
import com.amap.api.maps.model.*
import com.example.uav_client.Adapter.MapListAdapter
import com.example.uav_client.Application.SysApplication
import com.example.uav_client.Data.Common.NobodyAirplaneData
import com.example.uav_client.Data.Common.ReceiveBody
import com.example.uav_client.Data.Common.RequestBuildUtil
import com.example.uav_client.Data.Common.Station
import com.example.uav_client.Data.Main.DataListSource
import com.example.uav_client.Network.Consumer
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MapActivity : AppCompatActivity(), AMap.OnMapClickListener {
    lateinit var latLngClick: LatLng
    private lateinit var mapView: MapView
    private var aMap: AMap? = null
    private var markerOption: MarkerOptions? = null
    private var markerOption1: MarkerOptions? = null
    private var markerOption2: MarkerOptions? = null
    private var markerOptionCeju: MarkerOptions? = null
    private var markerOptionStation: MarkerOptions? = null
    private lateinit var marker: Marker
    private lateinit var marker1: Marker
    private lateinit var marker2: Marker
    private lateinit var markerCeju: Marker
    internal lateinit var biglayout: LinearLayout
    private lateinit var pushIma: ImageView
    internal lateinit var smalllayout: LinearLayout
    private lateinit var recyclerView: RecyclerView
    lateinit var measureLength: TextView
    lateinit var clearPoint: TextView
    lateinit var buffer: StringBuffer
    lateinit var screenShoot: TextView
    lateinit var mUiSettings: UiSettings
    lateinit var mapListAdapter: MapListAdapter
    var polygon: Polygon? = null
    var list = ArrayList<String>()
    lateinit var bg: View
    var latLngsAlarm: ArrayList<LatLng> = ArrayList()
    private val latLngsuser: MutableList<LatLng> = ArrayList()
    private val latLngs: MutableList<LatLng> = ArrayList()
    private val latLngs1: MutableList<LatLng> = ArrayList()
    private val latLngs2: MutableList<LatLng> = ArrayList()
    private val stationList: MutableList<Station> = ArrayList()
    private val stationMarkerList: MutableList<Marker> = ArrayList()


    private var downOrUp: Boolean = false
    override fun onMapClick(p0: LatLng?) {
        if (downOrUp) {
            click()
        } else {
            latLngClick = p0!!
            addMarkersToMapUser(latLngClick)
        }
    }

    override fun onBackPressed() {
        if (downOrUp) {
            click()
        } else {
            super.onBackPressed()
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

    var position = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val intent = getIntent()
        if (intent != null) {
            position = intent.getIntExtra("id", 0)
        }
        hideHeader()
        mapView = findViewById(R.id.map)
        bg = findViewById(R.id.bg)
        measureLength = findViewById(R.id.measure_length)
        clearPoint = findViewById(R.id.clear_point)
        screenShoot = findViewById(R.id.screenShot)
        mapDistanceTe = findViewById(R.id.map_util_distance)
        biglayout = findViewById(R.id.big_data_map_layout)
        smalllayout = findViewById(R.id.data_map_layout)
        pushIma = findViewById(R.id.map_data_up_down)
        recyclerView = findViewById(R.id.map_data_list)
        mapView.systemUiVisibility = View.INVISIBLE
        mapListAdapter = MapListAdapter(list)
        recyclerView.adapter = mapListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        measureLength.setOnClickListener {
            click()
        }
        clearPoint.setOnClickListener {
            clear()
        }
        screenShoot.setOnClickListener {
            shootScreen()
            bg.visibility = View.VISIBLE
        }
        pushIma.setOnClickListener {
            click()
        }
        mapView.onCreate(savedInstanceState)
        if (aMap == null) {
            aMap = mapView.map
            aMap!!.setOnMapClickListener(this)
            mUiSettings = aMap!!.uiSettings
            mUiSettings.isZoomControlsEnabled = false
            mUiSettings.isCompassEnabled = true
            mUiSettings.isScaleControlsEnabled = true
            mUiSettings.isGestureScaleByMapCenter = true
            val markerClickListener = AMap.OnMarkerClickListener { marker ->
                if (stationMarkerList.contains(marker)) {
                    popWindowStation(stationList[stationMarkerList.indexOf(marker)])
                    true
                } else {
                    false
                }
            }
            aMap!!.setOnMarkerClickListener(markerClickListener)
        }
        biglayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                var viewHeight = smalllayout.height
                biglayout.translationY = viewHeight.toFloat()
                biglayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        changeCamera(LatLng(30.632194819840947, 103.97716823318481), true)
    }

    internal lateinit var popupView: View
    internal lateinit var newWindow: PopupWindow
    internal var textView: TextView? = null

    fun popWindowStation(station: Station) {
        popupView = this.layoutInflater.inflate(R.layout.custom_info_window, null)
        popupView.setPadding(50, 0, 50, 0)
        newWindow = PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        newWindow.width = this.resources.getDimension(R.dimen.dp_280).toInt()
        newWindow.animationStyle = R.style.popup_window_anim
        newWindow.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))
        newWindow.isFocusable = false
        newWindow.isOutsideTouchable = true
        newWindow.update()
        newWindow.showAtLocation(this.window.decorView, Gravity.CENTER, 0, 0)
        textView = popupView.findViewById(R.id.text)
        textView!!.text = "ID：" + station.id + "\n" + "站名：" + station.name + "\n" + "状态：" + station.status + "\n" + "经度：" + station.lon + "\n" + "纬度：" + station.lat
    }


    override fun onResume() {
        super.onResume()
        delete = 0
        add = 0
        changeAtBegin()
        if (position == -1) {
            Consumer.addObserverMap(object : DataListSource.getDataCallBack {  //在线
                override fun dataGet(dataList: ByteArray) {
                    if (dataList.isEmpty()) {
                        drawArea(SysApplication.alarmArea)
                    } else {
                        var dataList1 = RequestBuildUtil.unPack(dataList)
                        var s = String(dataList1)
                        var ss = ReceiveBody.initialParse(s, "|")
                        loop@ for (i in 0..6) {
                            var string = ""
                            when (i) {
                                0 -> string = "无人机ID： " + ss[0]
                                1 -> string = "频率：" + ss[1]
                                2 -> string = "经度：" + ss[2]
                                3 -> string = "纬度：" + ss[3]
                                4 -> string = "俯仰角：" + ss[4]
                                5 -> string = "日期时间：" + ss[5]
                                6 -> string = "是否报警：" + ss[6]
                            }
                            if (string == "俯仰角：" + "-999") {
                                list.add("俯仰角：" + "---")
                            } else {
                                list.add(string)
                            }
                        }
                        drawOnMap(NobodyAirplaneData(ss[0], ss[1].toLong(), ss[2].toDouble(), ss[3].toDouble(), ss[4].toDouble(), ss[5], ss[6].toInt()), list, true)
                    }
                }

                override fun error() {
                }
            })
            Consumer.addMainObserverStation(object : DataListSource.getDataCallBack {
                override fun dataGet(dataList: ByteArray) {
                    if (dataList.isEmpty()) {
                        drawStation()
                    }
                }

                override fun error() {

                }
            })
        } else {
            Consumer.addUnLineObserverMap(object : DataListSource.getDataCallBack { //离线
                override fun dataGet(dataList: ByteArray) {
                    Log.d("xiaomap", String(dataList))
                    var s = String(dataList)
                    var ss = ReceiveBody.initialParse(s, "|")
                    loop@ for (i in 0..6) {
                        var string = ""
                        when (i) {
                            0 -> string = "无人机ID： " + ss[0]
                            1 -> string = "频率：" + ss[1]
                            2 -> string = "经度：" + ss[3]
                            3 -> string = "纬度：" + ss[2]
                            4 -> string = "俯仰角：" + ss[4]
                            5 -> string = "日期时间：" + ss[5]
                            6 -> string = "是否报警：" + ss[6]
                        }
                        if (string == "俯仰角：" + "-999") {
                            list.add("俯仰角：" + "---")
                        } else {
                            list.add(string)
                        }
                    }
                    try {
                        drawOnMap(NobodyAirplaneData(ss[0], ss[1].toLong(), ss[2].toDouble(), ss[3].toDouble(), ss[4].toDouble(), ss[5], ss[6].toInt()), list, false)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun error() {

                }
            })
            Consumer.UnonLine(SysApplication.datahash[position])
            measureLength.postDelayed({ click() }, 1000)
        }
        if (SysApplication.alarmArea.size > 0) {
            measureLength.postDelayed({ drawArea(SysApplication.alarmArea) }, 1000)
        }
        if (SysApplication.stationItem.size > 0) {
            measureLength.postDelayed({ drawStation() }, 1000)
        }
    }

    private fun drawStation() {
        for (item in stationMarkerList) {
            item.remove()
        }
        var lon = 0.0
        var lat = 0.0
        stationList.clear()
        stationMarkerList.clear()
        for (item in SysApplication.stationItem) {
            var list = ReceiveBody.initialParse(item, "|")
            stationList.add(Station(list[0], list[1], list[2], list[3].toDouble(), list[4].toDouble()))
            when (list[2]) {
                "正常" -> markerOptionStation = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.station_unuse))
                        .position(LatLng(list[4].toDouble(), list[3].toDouble()))
                        .draggable(false)
                "故障" -> markerOptionStation = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.station_icon_trouble))
                        .position(LatLng(list[4].toDouble(), list[3].toDouble()))
                        .draggable(false)
                "使用中" -> markerOptionStation = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.station_use))
                        .position(LatLng(list[4].toDouble(), list[3].toDouble()))
                        .draggable(false)
            }
            marker = aMap!!.addMarker(markerOptionStation)
            stationMarkerList.add(marker)
            lon = list[3].toDouble()
            lat = list[4].toDouble()
        }
        Toast.makeText(this, "站点数据成功加载到地图", Toast.LENGTH_SHORT).show()
        changeCamera(LatLng(lat, lon), false)
    }

    private fun changeAtBegin() {
        var sharedPreferences = this.getSharedPreferences("latlng", Context.MODE_PRIVATE)
        var lon = sharedPreferences.getFloat("lon", 0.0F)
        var lat = sharedPreferences.getFloat("lat", 0.0F)
        if (!lon.equals(0.0F)) {
            measureLength.postDelayed({ changeCamera(LatLng(lon.toDouble(), lat.toDouble()), true) }, 500)
        }
    }

    var idlist: MutableList<String> = ArrayList()
    fun drawOnMap(nobodyAirplaneData: NobodyAirplaneData, list: List<String>, isLine: Boolean) {
        changeCamera(LatLng(nobodyAirplaneData.lat, nobodyAirplaneData.lon), true)
        if (idlist.contains(nobodyAirplaneData.id)) {
            addMarkersToMap(LatLng(nobodyAirplaneData.lat, nobodyAirplaneData.lon), isLine, idlist.indexOf(nobodyAirplaneData.id))
        } else {
            idlist.add(nobodyAirplaneData.id)
            addMarkersToMap(LatLng(nobodyAirplaneData.lat, nobodyAirplaneData.lon), isLine, idlist.size - 1)
        }
//        addMarkersToMap(LatLng(nobodyAirplaneData.lat, nobodyAirplaneData.lon), isLine)
        mapListAdapter.setDataList(list)
        mapListAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        Consumer.addObserverMap(null)
        Consumer.addUnLineObserverMap(null)
        saveLatlng()
    }

    fun saveLatlng() {
        if (latLngs.size > 0) {
            var sharedPreferences = this.getSharedPreferences("latlng", Context.MODE_PRIVATE)
            var editor = sharedPreferences.edit()
            editor.putFloat("lon", latLngs[latLngs.size - 1].longitude.toFloat())
            editor.putFloat("lat", latLngs[latLngs.size - 1].latitude.toFloat())
        }
    }

    fun drawArea(latLngs: List<LatLng>) {
        if (polygon != null) {
            polygon!!.remove()
        }
        val polygonOptions1 = PolygonOptions()
                .fillColor(Color.parseColor("#11000000")).strokeColor(Color.RED).strokeWidth(4f)
        polygonOptions1.addAll(latLngs)
        polygon = aMap!!.addPolygon(polygonOptions1)
        changeCamera(latLngs[latLngs.lastIndex], false)
    }

    private fun shootScreen() {
        aMap!!.getMapScreenShot(object : AMap.OnMapScreenShotListener {
            override fun onMapScreenShot(bitmap: Bitmap) {

            }

            override fun onMapScreenShot(bitmap: Bitmap?, status: Int) {
                val sdf = SimpleDateFormat("yyyyMMddHHmmss")
                if (null == bitmap) {
                    return
                }
                try {
                    var file = getOutputMediaFile()
                    if (!file!!.parentFile.exists()) {
                        file.parentFile.mkdirs()
                    }
                    Log.d("xiao", file.absolutePath)
                    val fos = FileOutputStream(file)
                    val b = bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    try {
                        fos.flush()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    try {
                        fos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    buffer = StringBuffer()
                    if (b) {
                        buffer.append("截屏成功 ")
                        val saveAs = file.absolutePath
                        val contentUri = Uri.fromFile(File(saveAs))
                        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri)
                        sendBroadcast(mediaScanIntent)
                    } else {
                        buffer.append("截屏失败 ")
                    }
//                    if (status != 0)
//                        buffer.append("地图渲染完成，截屏无网格")
//                    else {
//                        buffer.append("地图未渲染完成，截屏有网格")
//                    }
                    Toast.makeText(this@MapActivity, buffer.toString(), Toast.LENGTH_SHORT).show()
                    bg.visibility = View.GONE
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    bg.visibility = View.GONE
                }

            }
        })
    }

    fun getOutputMediaFile(): File? {
        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val mediaStorageDir = File(getCameraPath(file))
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(Date())

        return File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".png")
    }

    private fun getCameraPath(dcim: File): String {//获得相册目录
        Log.d("xiaotao", dcim.path.toString())
        try {
            for (file in dcim.listFiles()) {
                if ("camera".equals(file.name, ignoreCase = true)) {
                    return file.absolutePath
                }
            }
        } catch (e: Exception) {
        }

        return dcim.absolutePath
    }

    private fun click() {
        if (!downOrUp) {
            biglayout.translationY = (smalllayout.height - recyclerView.height).toFloat()
            pushIma.setImageResource(R.drawable.push_down)
            downOrUp = true
        } else {
            downOrUp = false
            var viewHeight = smalllayout.height
            biglayout.translationY = viewHeight.toFloat()
            pushIma.setImageResource(R.drawable.pull_up)
        }
    }

    var firstin: Boolean = true
    private fun changeCamera(latLng: LatLng, room: Boolean) { //改变地图坐标位置
        var zoom = aMap!!.cameraPosition.zoom
        if (room) {
            if (firstin) {
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

    private fun drawLine(latlngs: MutableList<LatLng>) {  //画线
        polyline = aMap!!.addPolyline(PolylineOptions().addAll(latlngs).width(20f).color(Color.argb(140, 33, 77, 66)))
        changeCamera(latlngs[latlngs.lastIndex], false)
    }

    private fun drawLinePoint(latLng: MutableList<LatLng>, position: Int) {
        if (polylinemap != null) {
            polylinemap!!.remove()
        }
        when (position) {
            0 -> polylinemap = aMap!!.addPolyline(PolylineOptions().addAll(latLng).width(4f).color(Color.argb(140, 33, 33, 66)))
            1 -> polylinemap = aMap!!.addPolyline(PolylineOptions().addAll(latLng).width(4f).color(Color.argb(140, 66, 66, 166)))
            2 -> polylinemap = aMap!!.addPolyline(PolylineOptions().addAll(latLng).width(10f).color(Color.argb(140, 122, 122, 166)))
        }

    }


    private fun drawArc(latLng1: LatLng, latLng2: LatLng, latLng3: LatLng) {  //三个点画圆弧
        val arcOptions = ArcOptions().point(
                LatLng(30.632194819840947, 103.97716823318481), LatLng(30.63301643674847, 103.97646549442291),
                LatLng(30.632527160105045, 103.97847178676605)).strokeColor(Color.RED).strokeWidth(4f)
        aMap?.addArc(arcOptions)
    }

    var pointcan: Boolean = true
    var polyline: Polyline? = null
    var polylinemap: Polyline? = null
    private val markers: MutableList<Marker> = ArrayList()
    lateinit var mapDistanceTe: TextView
    private fun addMarkersToMapUser(latLng: LatLng) {  //定点化标志
        if (pointcan) {
            addToPoint(latLng)
        } else {
            clear()
            addToPoint(latLng)
        }
    }

    private fun clear() {
        latLngsuser.clear()
        if (polyline != null) {
            polyline!!.remove()
            for (marke in markers) {
                if (aMap != null) {
                    marke.destroy()
                }
            }
            pointcan = true
            mapDistanceTe.text = ""
        }
    }

    private fun addToPoint(latLng: LatLng) {
        latLngsuser.add(latLng)
        markerOptionCeju = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.point))
                .position(latLng)
                .draggable(true)
        markerCeju = aMap!!.addMarker(markerOptionCeju)
        markers.add(markerCeju)
        if (latLngsuser.size == 2) {
            drawLine(latLngsuser)
            pointcan = false
            var distance = AMapUtils.calculateLineDistance(latLngsuser[0], latLngsuser[1])
            mapDistanceTe.text = "  " + "两点距离为:" + distanceChange(distance).toString() + if (distance > 1000) "千米" else "米" + "  "
        }
    }

    private fun distanceChange(distance: Float): Float {
        if (distance > 1000) {
            return distance / 1000
        }
        return distance
    }

    var delete: Int = 0
    var add: Int = 0
    var markerlist: MutableList<Marker> = ArrayList()
    var markerlist1: MutableList<Marker> = ArrayList()
    var markerlist2: MutableList<Marker> = ArrayList()
    private fun addMarkersToMap(latLng: LatLng, isLine: Boolean, position: Int) {  //定点化标志

        if (position == 0) {
            for (item in markerlist) {
                item.remove()
            }
            latLngs.add(latLng)
            Log.d("xiaoxiaomm", "mmm" + latLngs.size)
            var num = latLngs.size
            loop@ for (i in 1..num) {
                Log.d("xiaoxiaomm", i.toString())
                if (latLngs.size - (num - i) > 20) {
                    break@loop
                }
                if ((num - i) == latLngs.size - 1) {
                    markerOption = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.airplane_small_map))
                            .position(latLngs[(num - i)])
                            .draggable(true)
                    marker = aMap!!.addMarker(markerOption)
                    markerlist.add(marker)
                } else {
                    markerOption = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.point))
                            .position(latLngs[(num - i)])
                            .draggable(true)
                    marker = aMap!!.addMarker(markerOption)
                    markerlist.add(marker)
                }
            }
        } else if (position == 2) {
            for (item in markerlist1) {
                item.remove()
            }
            latLngs1.add(latLng)
            Log.d("xiaoxiaomm", "mmm" + latLngs1.size)
            var num = latLngs1.size
            loop@ for (i in 1..num) {
                Log.d("xiaoxiaomm", i.toString())
                if (latLngs1.size - (num - i) > 20) {
                    break@loop
                }
                if ((num - i) == latLngs1.size - 1) {
                    markerOption1 = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.airplane_small_map))
                            .position(latLngs1[(num - i)])
                            .draggable(true)
                    marker1 = aMap!!.addMarker(markerOption1)
                    markerlist1.add(marker1)
                } else {
                    markerOption1 = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.point))
                            .position(latLngs1[(num - i)])
                            .draggable(true)
                    marker1 = aMap!!.addMarker(markerOption1)
                    markerlist1.add(marker1)
                }
            }
        } else if (position == 2){
            for (item in markerlist2) {
                item.remove()
            }
            latLngs2.add(latLng)
            Log.d("xiaoxiaomm", "mmm" + latLngs2.size)
            var num = latLngs2.size
            loop@ for (i in 1..num) {
                Log.d("xiaoxiaomm", i.toString())
                if (latLngs2.size - (num - i) > 20) {
                    break@loop
                }
                if ((num - i) == latLngs2.size - 1) {
                    markerOption2 = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.airplane_small_map))
                            .position(latLngs2[(num - i)])
                            .draggable(true)
                    marker2 = aMap!!.addMarker(markerOption2)
                    markerlist2.add(marker2)
                } else {
                    markerOption2 = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.point))
                            .position(latLngs2[(num - i)])
                            .draggable(true)
                    marker2 = aMap!!.addMarker(markerOption2)
                    markerlist2.add(marker2)
                }
            }
        }

            if (!isLine) {
                drawLinePoint(latLngs, position)
            }
//        Log.d("xiaohua", "画点")
//        if (latLngs.size > 0) {
//            delete++
//            Log.d("xiaohua", "delete$delete")
//            marker.remove()
//            markerOption = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.point))
//                    .position(latLngs[latLngs.size - 1])
//                    .draggable(true)
//            aMap!!.addMarker(markerOption)
//        }
//
//        markerOption = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.airplane_small_map))
//                .position(latLng)
//                .draggable(true)
//        marker = aMap!!.addMarker(markerOption)
//        add++
//        Log.d("xiaohua", "add$add")
//        Log.d("xiaohua", "+++++++++++++++++++++++++++++++++")
//        latLngs.add(latLng)
//        if (!isLine) {
//            drawLinePoint(latLngs)
//        }
    }

    private fun drawPoint() {
        val markerOption = MarkerOptions()
        markerOption.position(LatLng(30.632194819840947, 103.97716823318481))
        markerOption.title("西安市").snippet("西安市：34.341568, 108.940174")
        markerOption.draggable(true)//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, R.drawable.point))) // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.isFlat = true//设置marker平贴地图效果
    }

    fun bottomclick(view: View) {

    }

//    class MapInfoWindowAdapter(private var station: Station, internal var context: Context, private var stationList: MutableList<Station>, private var stationMarkerList: MutableList<Marker>) : AMap.InfoWindowAdapter {
//        private var stringStation: String = "站名：" + station.name + "\n" + "状态：" + station.status + "\n" + "经度：" + station.lon + "\n" + "纬度" + station.lat
//        private var infoWindow: View? = null
//        private var textView: TextView? = null
//
//
//        override fun getInfoContents(marker: Marker): View? {
//            return null
//            //示例没有采用该方法。
//        }
//
//        /**
//         * 监听自定义infowindow窗口的infowindow事件回调
//         */
//        override fun getInfoWindow(marker: Marker): View {
//            if(stationMarkerList.contains(marker)){
//                if (infoWindow == null) {
//                    infoWindow = LayoutInflater.from(context).inflate(
//                            R.layout.custom_info_window, null)
//                    textView = infoWindow!!.findViewById(R.id.text)
//                    textView!!.text = stringStation
//                }
//                render(marker, infoWindow!!)
//                return infoWindow as View
//            }else{
//                false
//            }
//            //加载custom_info_window.xml布局文件作为InfoWindow的样式
//            //该布局可在官方Demo布局文件夹下找到
//        }
//
//        /**
//         * 自定义infowinfow窗口
//         */
//        fun render(marker: Marker, view: View) {
//            //如果想修改自定义Infow中内容，请通过view找到它并修改
//        }
//    }

}