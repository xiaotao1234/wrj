package com.example.uav_client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class AlarmActivity : AppCompatActivity() {
    lateinit var topImage1:ImageView
    lateinit var topImage2:ImageView
    lateinit var topListView:RecyclerView
    var recordView:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        topImage1 = findViewById(R.id.time_spain)
        topImage2 = findViewById(R.id.location_spain)
        topImage1.systemUiVisibility = View.INVISIBLE
        topListView = findViewById(R.id.spin_list)
        topListView.viewTreeObserver.addOnGlobalLayoutListener {
            
        }
    }

    private fun topClick(i:Int) {
        recordView = i

    }
}
