package com.example.uav_client.View

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import android.graphics.Bitmap


class airplan_child(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {

    internal var width: Int = 0
    internal var height: Int = 0
    override fun onDraw(canvas: Canvas?) {
        dosomethingfornothing()
        when (airplanePath.direction) {
            1 -> {
                canvas!!.translate((width).toFloat(), (height / 2).toFloat())
                canvas!!.rotate(135F)
            }
            2 -> {
                canvas!!.translate(0F, (height/2).toFloat())
                canvas!!.rotate(45F)
                canvas!!.scale(1F, -1F)
            }
            3 -> {
                canvas!!.translate(0F, (height / 2).toFloat())
                canvas!!.rotate(-45F)
            }
            4 -> {
                canvas!!.translate((width / 2).toFloat(), 0F)
                canvas!!.rotate(45F)
            }
        }
        super.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        width = w
        height = h
    }

    private fun dosomethingfornothing() {
//        Log.d("xiaoxiaoxiao", "dosomethingfornothing")
    }
}