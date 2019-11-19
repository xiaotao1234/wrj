package com.example.uav_client

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uav_client.R
import android.os.Build
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uav_client.Adapter.UserManagerAdapter
import com.example.uav_client.Contracts.MainTaskDetailContract
import com.example.uav_client.Data.Common.User
import com.example.uav_client.Prensenters.MainPresenter
import com.example.uav_client.View.refreshV
import java.util.*
import kotlin.collections.ArrayList


class UserManagerActivity : AppCompatActivity(),MainTaskDetailContract.View{
    override fun error() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    internal lateinit var search: ImageView
    internal lateinit var addUser: ImageView
    internal lateinit var back: ImageView
    internal lateinit var reV: refreshV
    internal lateinit var supertext: TextView
    internal lateinit var supertext1: TextView
    internal lateinit var normaltext: TextView
    internal lateinit var normaltext1: TextView
    internal lateinit var userList: RecyclerView
    internal lateinit var userManagerAdapter: UserManagerAdapter
    internal var userListdata: MutableList<User> = ArrayList()
    lateinit var presenter:MainPresenter

    override fun showList(dataList: ByteArray) {
        var s2 = String(dataList)

    }

    override fun release() {

    }
    var handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg!!.what) {
                1 -> {
                    userManagerAdapter.setList(msg?.obj as List<User>)
                    userManagerAdapter.notifyDataSetChanged()
                }
                2 -> {
                    userManagerAdapter.setList(msg?.obj as List<User>)
                    userManagerAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_manager)
        hideHeader()
        initView()
        initData()
    }

    private fun initData() {
        presenter = MainPresenter(this)
        presenter.getData("",7)
    }

    private fun initView() {
        search = findViewById(R.id.search)
        addUser = findViewById(R.id.add_user)
        back = findViewById(R.id.back)
        reV = findViewById(R.id.ref_layout)
        userList = findViewById(R.id.user_list)
        userList.layoutManager = LinearLayoutManager(this)
        loop@ for (i in 1..10) {
            userListdata.add(User("xiao", "tao", User.SUPER_USER,0,1))
            userListdata.add(User("xiao", "tao", User.NORMAL_USER,0,1))
        }
        userManagerAdapter = UserManagerAdapter(userListdata)
        userManagerAdapter.setLayoutClickListener(object : UserManagerAdapter.LayoutClickListener {
            override fun callback(position: Int) {
                popWindowUser(reV, position)
            }
        })
        userList.adapter = userManagerAdapter
//        userList.adapter = userManagerAdapter
        back.setOnClickListener {
            finish()
        }
        addUser.setOnClickListener {
            addView()
            popWindow(reV)
        }
    }

    private fun addView() {
//        val view:View = View(this)
//        view.setBackgroundColor(Color.parseColor("#22000000"))
//        val lapare:FrameLayout.LayoutParams = FrameLayout.LayoutParams(200)
//        reV.addView(view,1,)
    }

    private fun hideHeader() {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun popWindowUser(parent: ViewGroup, position: Int) {
        val popupView = this@UserManagerActivity.layoutInflater.inflate(R.layout.popwindow_user, null)
        popupView.setPadding(50, 0, 50, 0)
        val window = PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        window.width = resources.getDimension(R.dimen.dp_280).toInt()
        window.animationStyle = R.style.popup_window_anim
        window.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))
        window.isFocusable = true
        window.isOutsideTouchable = true
        window.update()
        window.showAtLocation(parent, Gravity.CENTER_VERTICAL, 0, 0)
        var addUser = popupView.findViewById<TextView>(R.id.add_button)
        var userName = popupView.findViewById<EditText>(R.id.user_edit)
        var userPassword = popupView.findViewById<EditText>(R.id.password_edit)
        userName.setText(userListdata[position].name)
        userPassword.setText(userListdata[position].password)
        supertext1 = popupView.findViewById(R.id.super_change)
        normaltext1 = popupView.findViewById(R.id.normal_change)
        if (userListdata[position].type.equals(User.NORMAL_USER)) {
            normaltext1.setBackgroundResource(R.drawable.button_bg)
            supertext1.background = null
        } else {
            normaltext1.background = null
            supertext1.setBackgroundResource(R.drawable.button_bg)
        }
        supertext1.setOnClickListener {
            normaltext1.background = null
            supertext1.setBackgroundResource(R.drawable.button_bg)
        }
        normaltext1.setOnClickListener {
            normaltext1.setBackgroundResource(R.drawable.button_bg)
            supertext1.background = null
        }
        addUser.setOnClickListener {
            val stringName: String = userName.text.toString()
            val stringPassword: String = userPassword.text.toString()
            Toast.makeText(this, "$stringName|$stringPassword", Toast.LENGTH_SHORT).show()
        }
    }

    private fun popWindow(parent: ViewGroup) {
        val popupView = this@UserManagerActivity.layoutInflater.inflate(R.layout.popupwindow, null)
        popupView.setPadding(50, 0, 50, 0)
        val window = PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        window.width = resources.getDimension(R.dimen.dp_280).toInt()
        window.animationStyle = R.style.popup_window_anim
        window.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))
        window.isFocusable = true
        window.isOutsideTouchable = true
        window.update()
        window.showAtLocation(parent, Gravity.CENTER_VERTICAL, 0, 0)
        var addUser = popupView.findViewById<TextView>(R.id.add_button)
        var userName = popupView.findViewById<EditText>(R.id.user_edit)
        var userPassword = popupView.findViewById<EditText>(R.id.password_edit)
        supertext = popupView.findViewById(R.id.super_change)
        normaltext = popupView.findViewById(R.id.normal_change)
        supertext.setOnClickListener {
            normaltext.background = null
            supertext.setBackgroundResource(R.drawable.button_bg)
        }
        normaltext.setOnClickListener {
            normaltext.setBackgroundResource(R.drawable.button_bg)
            supertext.background = null
        }
        addUser.setOnClickListener {
            val stringName: String = userName.text.toString()
            val stringPassword: String = userPassword.text.toString()
            Toast.makeText(this, "$stringName|$stringPassword", Toast.LENGTH_SHORT).show()
        }
    }
}
