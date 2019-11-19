package com.example.uav_client.View

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.example.uav_client.R

class refreshV : FrameLayout {
    internal var down: Int = 0
    internal var up: Int = 0
    internal var move: Int = 0
    internal var dp_100 = 0
    internal var upbz: Boolean = false
    internal lateinit var searchView: SearchView
    internal var flag: Boolean = true

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }

    private fun init() {
        View.inflate(context, R.layout.head_layout, this)
        searchView = findViewById(R.id.tv_head_view)
        dp_100 = resources.getDimension(R.dimen.dp_100).toInt()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        var re = false
        when (ev!!.action) {
            MotionEvent.ACTION_DOWN -> {
                down = ev.y.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                if (top) {
                    move = ev.y.toInt()
                    if (move - down > 0) {
                        upbz = true
                        re = true
                    } else {
                        re = false
                    }
                } else {
                    re = false
                }
            }
            MotionEvent.ACTION_UP -> {
                if (upbz && ev.y.toInt() - down > dp_100) {
                    headListener.callback()
                }
            }
        }
        return re
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                down = event.y.toInt()
                up = 0
            }
            MotionEvent.ACTION_UP -> {
                up = event.y.toInt()
                if (up - down > dp_100) {
                    headListener!!.callback()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    interface HeadListener {
        fun callback()
    }

    lateinit var headListener: HeadListener
    internal var top: Boolean = false

    internal fun Top() {
        top = true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (flag) {
            canvas!!.drawColor(Color.parseColor("#22000000"))
        }
    }

    internal fun setListener(headListener: HeadListener) {
        this.headListener = headListener
    }
}