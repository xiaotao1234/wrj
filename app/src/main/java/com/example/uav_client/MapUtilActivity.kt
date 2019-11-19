package com.example.uav_client

import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.maps.*
import com.amap.api.maps.model.*
import com.example.uav_client.Data.Main.MainDataInfo
import android.graphics.Bitmap.CompressFormat
import android.os.Environment.getExternalStorageDirectory
import com.amap.api.maps.AMap.OnMapScreenShotListener
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Build
import android.os.Environment
import android.provider.SyncStateContract
import android.view.inputmethod.EditorInfo
import android.widget.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MapUtilActivity : AppCompatActivity(), AMap.OnMapClickListener {
    private lateinit var mapView: MapView
    lateinit var latLngClick: LatLng
    private var aMap: AMap? = null
    private var markerOption: MarkerOptions? = null
    var pointcan: Boolean = true
    lateinit var mUiSettings: UiSettings
    private lateinit var marker: Marker
    lateinit var measureLength: TextView
    lateinit var polyline: Polyline
    lateinit var clearPoint: TextView
    lateinit var screenShoot: TextView
    lateinit var confirmText: TextView
    lateinit var lonedit:EditText
    lateinit var latedit:EditText
    lateinit var buffer: StringBuffer
    lateinit var bg: View
    lateinit var mapDistanceTe: TextView
    lateinit var polygon:Polygon
    lateinit var mapCentertext: TextView
    private val latLngs: MutableList<LatLng> = ArrayList()
    private val markers: MutableList<Marker> = ArrayList()
//        get() {
//            val latLngs = ArrayList<LatLng>()
//            return latLngs
//        }

    override fun onMapClick(p0: LatLng?) {
        latLngClick = p0!!
        addMarkersToMap(latLngClick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_util)
        hideHeader()
        mapView = findViewById(R.id.map1)
        mapView.systemUiVisibility = View.INVISIBLE
        measureLength = findViewById(R.id.measure_length)
        clearPoint = findViewById(R.id.clear_point)
        screenShoot = findViewById(R.id.screenShot)
        mapCentertext = findViewById(R.id.map_center_ll)
        confirmText = findViewById(R.id.confirm)
        lonedit = findViewById(R.id.lon)
        latedit = findViewById(R.id.lat)
        lonedit.setInputType(EditorInfo.TYPE_CLASS_PHONE)
        latedit.setInputType(EditorInfo.TYPE_CLASS_PHONE)
        bg = findViewById(R.id.bg)
        mapDistanceTe = findViewById(R.id.map_util_distance)
        confirmText.setOnClickListener {
            changeCamera(LatLng(lonedit.text.toString().toDouble(),latedit.text.toString().toDouble()))
            toGeoLocation()
        }
        mapView.onCreate(savedInstanceState)
        if (aMap == null) {
            aMap = mapView.map
            aMap!!.setOnMapClickListener(this)
            mUiSettings = aMap!!.getUiSettings()
            mUiSettings.setZoomControlsEnabled(false)
        }
        changeCamera(LatLng(30.632194819840947, 103.97716823318481))
//        addMarkersToMap(LatLng(30.632194819840947, 103.97716823318481))
        measureLength.setOnClickListener {
            Toast.makeText(this, "请在地图上选定测距点", Toast.LENGTH_SHORT).show()
        }
        clearPoint.setOnClickListener {
            if(latLngs.size>0){
                clear()
            }
        }
        screenShoot.setOnClickListener {
            shootScreen()
            bg.visibility = View.VISIBLE
        }
        aMap!!.setOnMapTouchListener {
            when(it.action){
                MotionEvent.ACTION_MOVE -> toGeoLocation()
            }
        }
        toGeoLocation()
    }

    private fun hideHeader() {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.parseColor("#FFFFFF")
        }
    }



//    fun customMarker() {
//        var markerOption = MarkerOptions()
//        markerOption.position(SyncStateContract.Constants.XIAN)
//        markerOption.title("西安市").snippet("西安市：34.341568, 108.940174")
//
//        markerOption.draggable(true)//设置Marker可拖动
//        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                .decodeResource(resources, R.drawable.location_marker)))
//        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
//        markerOption.setFlat(true)//设置marker平贴地图效果
//    }

    private fun shootScreen() {
        aMap!!.getMapScreenShot(object : OnMapScreenShotListener {
            override fun onMapScreenShot(bitmap: Bitmap) {

            }

            override fun onMapScreenShot(bitmap: Bitmap?, status: Int) {
                val sdf = SimpleDateFormat("yyyyMMddHHmmss")
                if (null == bitmap) {
                    return
                }
                try {
                    var file = File(getExternalStorageDirectory().absolutePath + File.separator + "picturemap" + File.separator
                            + sdf.format(Date()) + ".png")
                    if (!file.parentFile.exists()) {
                        file.parentFile.mkdirs()
                        file.mkdirs()
                    }
                    Log.d("xiao", file.absolutePath)
                    val fos = FileOutputStream(file)
                    val b = bitmap!!.compress(CompressFormat.PNG, 100, fos)
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
                    Toast.makeText(this@MapUtilActivity, buffer.toString(), Toast.LENGTH_SHORT).show()
                    bg.visibility = View.GONE
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    bg.visibility = View.GONE
                }

            }
        })
    }

    private fun toGeoLocation() {
        var widthmap = mapView.width
        var heightmap = mapView.height
        var l:LatLng
        l = aMap?.projection?.fromScreenLocation(Point(widthmap/2,heightmap/2))!!
        mapCentertext.setText("地图中心坐标   "+"经度："+l.longitude+"  "+"纬度： "+l.latitude)
    }

    private fun clear() {
        latLngs.clear()
        if(polyline!=null){
            polyline.remove()
        }
        for (marke in markers) {
            if (aMap != null) {
                marke.destroy()
            }
        }
        pointcan = true
        mapDistanceTe.setText("")
    }

    private fun changeCamera(latLng: LatLng) { //改变地图坐标位置
        aMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(
                CameraPosition.builder()
                        .target(latLng)
                        .tilt(18f)//目标区域倾斜度
                        .zoom(18f)//缩放级别
                        .bearing(30f)//旋转角度
                        .build()))
    }

    private fun drawLine(latlngs: MutableList<LatLng>) {  //画线
        polyline = aMap!!.addPolyline(PolylineOptions().addAll(latlngs).width(14f).color(Color.argb(140, 33, 77, 66)))
    }

    private fun drawArc(latLng1: LatLng, latLng2: LatLng, latLng3: LatLng) {  //三个点画圆弧
        val arcOptions = ArcOptions().point(
                LatLng(30.632194819840947, 103.97716823318481), LatLng(30.63301643674847, 103.97646549442291),
                LatLng(30.632527160105045, 103.97847178676605)).strokeColor(Color.RED).strokeWidth(4f)
        aMap?.addArc(arcOptions)
    }

    fun addPointMark(point: Point) {
        Log.d("xiaoxiao", point.toString())
        aMap?.getProjection()?.fromScreenLocation(point)
    }

    private fun addMarkersToMap(latLng: LatLng) {  //定点化标志
        if (pointcan == true) {
            addToPoint(latLng)
        } else {
            clear()
            addToPoint(latLng)
        }
    }

    private fun addToPoint(latLng: LatLng) {
        latLngs.add(latLng)
        markerOption = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.point))
                .position(latLng)
                .draggable(true)
        marker = aMap!!.addMarker(markerOption)
        markers.add(marker)
        if (latLngs.size == 2) {
            drawLine(latLngs)
            pointcan = false
            var distance = AMapUtils.calculateLineDistance(latLngs[0], latLngs[1])
//            Toast.makeText(this, distance.toString() + "米", Toast.LENGTH_SHORT).show()
            mapDistanceTe.setText("  " + "两点距离为:" + distanceChange(distance).toString() + if (distance > 1000) "千米" else "米" + "  ")
        }
    }

    private fun drawPoint() {
        val markerOption = MarkerOptions()
        markerOption.position(LatLng(30.632194819840947, 103.97716823318481))
        markerOption.title("西安市").snippet("西安市：34.341568, 108.940174")
        markerOption.draggable(true)//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, R.drawable.point))) // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.isFlat = true//设置marker平贴地图效果
    }

    private fun distanceChange(distance: Float): Float {
        if (distance > 1000) {
            var dis: Float
            dis = distance / 1000
            return dis
        }
        return distance
    }

//    private fun drawLine(list: List<LatLng>){
//        val options = PolylineOptions()
//        options.width(20f)//设置宽度
//        for(la in list){
//            options.add(la)
//        }
//        aMap!!.addPolyline(options)
//    }
}
