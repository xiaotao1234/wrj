package com.example.uav_client.Application

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.example.uav_client.R

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun popWindow() {
        val popupView = this@BaseActivity.layoutInflater.inflate(R.layout.popupwindow, null)
        popupView.setPadding(50, 0, 50, 0)
        var newWindow = PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        newWindow.width = resources.getDimension(R.dimen.dp_280).toInt()
        newWindow.animationStyle = R.style.popup_window_anim
        newWindow.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))
        newWindow.isFocusable = true
        newWindow.isOutsideTouchable = true
        newWindow.update()
        newWindow.showAtLocation(window.decorView, Gravity.CENTER_VERTICAL, 0, 0)
    }
}
