package com.example.uav_client

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
import com.example.uav_client.Data.Main.DataListSource
import com.example.uav_client.Network.Consumer
import com.example.uav_client.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MapActivity : AppCompatActivity(), AMap.OnMapClickListener {
    lateinit var latLngClick: LatLng
    private lateinit var mapView: MapView
    private var aMap: AMap? = null
    private var markerOption: MarkerOptions? = null
    private lateinit var marker: Marker
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
    private val latLngs: ArrayList<LatLng>
        get() {
            val latLngs = ArrayList<LatLng>()
            return latLngs
        }


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
        val  intent = getIntent()
        if(intent!=null){
            position = intent.getIntExtra("id",0)
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
            //            Toast.makeText(this, "请在地图上选定测距点", Toast.LENGTH_SHORT).show()
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
            mUiSettings = aMap!!.getUiSettings()
            mUiSettings.setZoomControlsEnabled(false)
        }
//        aMap?.setMapType(AMap.MAP_TYPE_SATELLITE)
        biglayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                var viewHeight = smalllayout.height
                biglayout.translationY = viewHeight.toFloat()
                biglayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        changeCamera(LatLng(30.632194819840947, 103.97716823318481), true)
    }

    override fun onResume() {
        super.onResume()
        if(position == -1){
            Consumer.addObserverMap(object : DataListSource.getDataCallBack {  //在线
                override fun dataGet(dataList: ByteArray) {
                    if (dataList.size == 0) {
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
                            list.add(string)
                        }
                        drawOnMap(NobodyAirplaneData(ss[0], ss[1].toLong(), ss[2].toDouble(), ss[3].toDouble(), ss[4].toDouble(), ss[5], ss[6].toInt()), list)
                    }
                }

                override fun error() {
                }
            })
        }else{
            Consumer.addUnLineObserverMap(object : DataListSource.getDataCallBack {//离线
            override fun dataGet(dataList: ByteArray) {
                Log.d("xiaomap", String(dataList))
                var s = String(dataList)
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
                    list.add(string)
                }
                drawOnMap(NobodyAirplaneData(ss[0], ss[1].toLong(), ss[2].toDouble(), ss[3].toDouble(), ss[4].toDouble(), ss[5], ss[6].toInt()), list)
            }

                override fun error() {

                }
            })
            Consumer.UnonLine(SysApplication.datahash[0])
            if (SysApplication.alarmArea.size > 0) {
//            drawLine(SysApplication.alarmArea)
                drawArea(SysApplication.alarmArea)
            }
        }
    }

    fun drawOnMap(nobodyAirplaneData: NobodyAirplaneData, list: List<String>) {
        changeCamera(LatLng(nobodyAirplaneData.lat, nobodyAirplaneData.lon), true)
        addMarkersToMap(LatLng(nobodyAirplaneData.lat, nobodyAirplaneData.lon))
        mapListAdapter.setDataList(list)
        mapListAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        Consumer.addObserverMap(null)
        Consumer.addUnLineObserverMap(null)
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
                    var file = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "picturemap" + File.separator
                            + sdf.format(Date()) + ".png")
                    if (!file.parentFile.exists()) {
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
                    if (b)
                        buffer.append("截屏成功 ")
                    else {
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

    private fun changeCamera(latLng: LatLng, room: Boolean) { //改变地图坐标位置

//        val builder = CameraPosition.builder().target(latLng)
//                .tilt(18f)//目标区域倾斜度
//                .zoom(14f)//缩放级别
//                .bearing(30f)//旋转角度
//
//        val cls = builder.javaClass
//        val field = cls.getDeclaredField("zoom")
//        field.isAccessible = true
//        var ss = field.get(builder)
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

    private fun drawLine(latlngs: MutableList<LatLng>) {  //画线
        polyline = aMap!!.addPolyline(PolylineOptions().addAll(latlngs).width(20f).color(Color.argb(140, 33, 77, 66)))
//        addMarkersToMap(latlngs[1])
        changeCamera(latlngs[latlngs.lastIndex], false)
    }

    private fun drawArc(latLng1: LatLng, latLng2: LatLng, latLng3: LatLng) {  //三个点画圆弧
        val arcOptions = ArcOptions().point(
                LatLng(30.632194819840947, 103.97716823318481), LatLng(30.63301643674847, 103.97646549442291),
                LatLng(30.632527160105045, 103.97847178676605)).strokeColor(Color.RED).strokeWidth(4f)
        aMap?.addArc(arcOptions)
    }

    var pointcan: Boolean = true
    var polyline: Polyline? = null
    private val markers: MutableList<Marker> = ArrayList()
    lateinit var mapDistanceTe: TextView
    private fun addMarkersToMapUser(latLng: LatLng) {  //定点化标志
        if (pointcan == true) {
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
            mapDistanceTe.setText("")
        }
    }

    private fun addToPoint(latLng: LatLng) {
        latLngsuser.add(latLng)
        markerOption = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.point))
                .position(latLng)
                .draggable(true)
        marker = aMap!!.addMarker(markerOption)
        markers.add(marker)
        if (latLngsuser.size == 2) {
            drawLine(latLngsuser)
            pointcan = false
            var distance = AMapUtils.calculateLineDistance(latLngsuser[0], latLngsuser[1])
//            Toast.makeText(this, distance.toString() + "米", Toast.LENGTH_SHORT).show()
            mapDistanceTe.setText("  " + "两点距离为:" + distanceChange(distance).toString() + if (distance > 1000) "千米" else "米" + "  ")
        }
    }

    private fun distanceChange(distance: Float): Float {
        if (distance > 1000) {
            var dis: Float
            dis = distance / 1000
            return dis
        }
        return distance
    }

    private fun addMarkersToMap(latLng: LatLng) {  //定点化标志
        latLngs.add(latLng)
        markerOption = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.point))
                .position(latLng)
                .draggable(true)
        marker = aMap!!.addMarker(markerOption)
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
}
