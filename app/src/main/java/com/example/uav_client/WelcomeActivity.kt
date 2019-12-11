package com.example.uav_client

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.uav_client.R

class WelcomeActivity : AppCompatActivity() {
    internal lateinit var welcomeImg: ImageView
    internal lateinit var handler: Handler
    lateinit var mAnimator: ObjectAnimator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        hideHeader()
        welcomeImg = findViewById(R.id.wel_img)
//        start()
        playLogoAnim()
    }

    private fun hideHeader() {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.parseColor("#FFFFFF")
        }
    }

    private fun start() {
        val handler = Handler()
        handler.postDelayed(Loading(), 1000)
    }

    internal inner class Loading : Thread() {
        override fun run() {
            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this@WelcomeActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@WelcomeActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
                } else {
                    goActivity(LoginActivity::class.java)
                }
            } else {
                finish()
            }
            2 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goActivity(LoginActivity::class.java)
            } else {
                finish()
            }
        }
    }

    private fun playLogoAnim() {
        mAnimator = ObjectAnimator.ofFloat(welcomeImg, "alpha", 0f, 1f)
        mAnimator.duration = 2500
        mAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                welcomeImg.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (ContextCompat.checkSelfPermission(this@WelcomeActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@WelcomeActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
                } else {
                    startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
                    finish()
                }
            }
        })
        mAnimator.start()
    }

    private fun goActivity(cls: Class<*>) {
        startActivity(Intent(this@WelcomeActivity, cls))
        finish()
    }
}
