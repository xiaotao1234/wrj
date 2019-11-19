package com.example.uav_client.View

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import com.example.uav_client.R


class airplanePath : ViewGroup, Runnable {
    internal var speedX = 3
    internal var speedY = 3
    internal var FX = 1
    internal var FY = 1
    internal var X = 0
    internal var Y = 0
    internal var left: Int = 0
    internal var right: Int = 0
    internal var top: Int = 0
    internal var bottom: Int = 0
    internal var width: Int = 0
    internal var height: Int = 0
    internal var childWidth: Int = 0
    internal var childHeight: Int = 0
    internal var paint: Paint
    internal var linearGradient: LinearGradient
    internal var handler = Handler(Looper.getMainLooper())
    internal var flagstop = false

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
//        setWillNotDraw(false)
        paint = Paint()
        paint.strokeWidth = resources.getDimension(R.dimen.dp_5)
        linearGradient = LinearGradient(0F, 0F, 0F, 0F, -0xdd7734, 0x00FFFFFF, Shader.TileMode.REPEAT)
        paint.setShader(linearGradient)
    }

    override fun run() {
        if (!flagstop) {
            X += FX * speedX
            Y += FY * speedY
            if (X + childWidth > width || X < 0) {
                FX = -FX
            }
            if (Y + childHeight > height || Y < 0) {
                FY = -FY
            }
            if (FX > 0 && FY > 0) {
                direction = 1
            } else if (FX > 0 && FY < 0) {
                direction = 4
            } else if (FX < 0 && FY > 0) {
                direction = 2
            } else {
                direction = 3
            }
            processTouch()
        }
        handler.postDelayed(this, 10)
    }

    companion object {
        @JvmStatic
        var direction: Int = 1

    }

    @SuppressLint("WrongCall")
    private fun processTouch() {
        onLayout(false, left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var view = getChildAt(0)
        childWidth = view.measuredWidth
        childHeight = view.measuredHeight
        view.layout(X, Y, X + view.measuredWidth, Y + view.measuredHeight)
        view.invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        width = w
        height = h
    }

    override fun onDraw(canvas: Canvas?) {
        dosomethingfornothing()
        super.onDraw(canvas)
//        canvas!!.drawPath()
    }

    fun dosomethingfornothing() {
        Log.d("xiaoxiaoxiao", "ondraw")
    }
}