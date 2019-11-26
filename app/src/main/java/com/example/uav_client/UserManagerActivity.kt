package com.example.uav_client

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uav_client.Adapter.UserManagerAdapter
import com.example.uav_client.Application.BaseActivity
import com.example.uav_client.Contracts.MainTaskDetailContract
import com.example.uav_client.Data.Common.ReceiveBody
import com.example.uav_client.Data.Common.RequestBuildUtil
import com.example.uav_client.Data.Common.User
import com.example.uav_client.Prensenters.MainPresenter
import com.example.uav_client.View.refreshV
import kotlin.collections.ArrayList


class UserManagerActivity : BaseActivity(), MainTaskDetailContract.View {
    override fun error() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    internal lateinit var search: ImageView
    internal lateinit var addUser: ImageView
    internal lateinit var back: ImageView
    internal lateinit var reV: refreshV
    internal lateinit var supertext: TextView
    internal lateinit var supertext1: TextView
    internal lateinit var deletetext: TextView
    internal lateinit var normaltext: TextView
    internal lateinit var normaltext1: TextView
    lateinit var view: View
    lateinit var newWindow: PopupWindow
    lateinit var modifyWindow: PopupWindow
    internal lateinit var userList: RecyclerView
    internal lateinit var userManagerAdapter: UserManagerAdapter

    var presenter: MainPresenter? = null
    lateinit var thread: Thread

    companion object {
        internal var userListdata: MutableList<User> = ArrayList()
    }

    override fun showList(dataList: ByteArray, requestCode: Int) {
        if (requestCode == RequestBuildUtil.SEARCH_USER_LIST_RESULT) {
            userListdata.clear()
            var s = String(dataList)
            var runnable = Runnable {
                var list = ReceiveBody.initialParse(s,";")
                for (item in list) {
                    var list1 = ReceiveBody.initialParse(item,"|")
                    userListdata.add(User(list1[0], list1[1], list1[2], list1[3].toInt(), list1[4]))
                }
                handler.sendEmptyMessage(1)
            }
            thread = Thread(runnable)
            thread.start()
        } else if (requestCode == RequestBuildUtil.ADD_USER_RESULT) {
            var s = String(dataList)
            var list1 = ReceiveBody.initialParse(s,"|")
            if (list1[1].toInt() == 1) {
                if (list1[0].toInt() == 1 && list1.size >= 7) {
                    Log.d("datachange", 1.toString())
                    userListdata.add(User(list1[2], list1[3], list1[4], list1[5].toInt(), list1[6]))
                    handler.sendEmptyMessage(1)
                    newWindow.dismiss()
                    view.visibility = View.GONE
                } else if (list1[0].toInt() == 2 && list1.size >= 3) {
                    Log.d("datachange", 2.toString())
                    userListdata.removeAt(userListdata.indexOf(User(list1[2], "", "", 1, "")))
                    handler.sendEmptyMessage(1)
                    modifyWindow.dismiss()
                    view.visibility = View.GONE
                } else if (list1[0].toInt() == 3 && list1.size >= 7) {
                    Log.d("datachange", 3.toString())
                    userListdata.set(userListdata.indexOf(User(list1[2], "", "", 1, ""))
                            , User(list1[2], list1[3], list1[4], list1[5].toInt(), list1[6]))
                    handler.sendEmptyMessage(1)
                    modifyWindow.dismiss()
                    view.visibility = View.GONE
                }
            } else {
                Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun release() {

    }

    var handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg!!.what) {
                1 -> {
                    userManagerAdapter.setList(userListdata)
                    userManagerAdapter.notifyDataSetChanged()
                }
                2 -> {
                    userManagerAdapter.setList(msg.obj as List<User>)
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
        presenter!!.getData("", RequestBuildUtil.SEARCH_USER_LIST)
    }

    private fun addUser(name: String, password: String, type: String) {
        if (presenter == null) {
            presenter = MainPresenter(this)
        }
        val s = "1|0|$name|$password|51|$type"
        presenter!!.getData(s, RequestBuildUtil.ADD_USER)
    }

    private fun deleteUser(id: String) {
        if (presenter == null) {
            presenter = MainPresenter(this)
        }
        val s = "2|$id|1|1|1|1"
        presenter!!.getData(s, RequestBuildUtil.ADD_USER)
    }

    private fun modificationUser(name: String, password: String, type: String, id: String) {
        if (presenter == null) {
            presenter = MainPresenter(this)
        }
        val s = "3|$id|$name|$password|51|$type"
        presenter!!.getData(s, RequestBuildUtil.ADD_USER)
    }

    private fun initView() {
        search = findViewById(R.id.search)
        addUser = findViewById(R.id.add_user)
        back = findViewById(R.id.back)
        reV = findViewById(R.id.ref_layout)
        userList = findViewById(R.id.user_list)
        view = findViewById(R.id.cap)
        userList.layoutManager = LinearLayoutManager(this)
        userManagerAdapter = UserManagerAdapter(userListdata)
        view.setOnClickListener {
            view.visibility = View.GONE
        }
        view.visibility = View.GONE
        userManagerAdapter.setLayoutClickListener(object : UserManagerAdapter.LayoutClickListener {
            override fun callback(position: Int) {
                popWindowUser(reV, position)
            }
        })
        userList.adapter = userManagerAdapter
        back.setOnClickListener {
            finish()
        }
        addUser.setOnClickListener {
            popWindow(reV)
        }
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
        view.visibility = View.VISIBLE
        var type: String = User.SUPER_USER
        var popupView = this@UserManagerActivity.layoutInflater.inflate(R.layout.popwindow_user, null)
        popupView.setPadding(50, 0, 50, 0)
        modifyWindow = PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        modifyWindow.width = resources.getDimension(R.dimen.dp_280).toInt()
        modifyWindow.animationStyle = R.style.popup_window_anim
        modifyWindow.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))
        modifyWindow.isFocusable = true
        modifyWindow.isOutsideTouchable = true
        modifyWindow.update()
        modifyWindow.showAtLocation(parent, Gravity.CENTER_VERTICAL, 0, 0)
        var modifyUser = popupView.findViewById<TextView>(R.id.add_button)
        var userName = popupView.findViewById<EditText>(R.id.user_edit)
        var userPassword = popupView.findViewById<EditText>(R.id.password_edit)
        userName.setText(userListdata[position].name)
        userPassword.setText(userListdata[position].password)
        supertext1 = popupView.findViewById(R.id.super_change)
        deletetext = popupView.findViewById(R.id.delete_button)
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
            type = User.SUPER_USER
            supertext1.setBackgroundResource(R.drawable.button_bg)
        }
        normaltext1.setOnClickListener {
            type = User.NORMAL_USER
            normaltext1.setBackgroundResource(R.drawable.button_bg)
            supertext1.background = null
        }
        deletetext.setOnClickListener {
            deleteUser(userListdata[position].id)
            initData()
        }
        modifyUser.setOnClickListener {
            val stringName: String = userName.text.toString()
            val stringPassword: String = userPassword.text.toString()
            modificationUser(stringName, stringPassword, type, userListdata[position].id)
            initData()
        }
    }

    private fun popWindow(parent: ViewGroup) {
        view.visibility = View.VISIBLE
        var type: String = User.SUPER_USER
        val popupView = this@UserManagerActivity.layoutInflater.inflate(R.layout.popupwindow, null)
        popupView.setPadding(50, 0, 50, 0)
        newWindow = PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        newWindow.width = resources.getDimension(R.dimen.dp_280).toInt()
        newWindow.animationStyle = R.style.popup_window_anim
        newWindow.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))
        newWindow.isFocusable = true
        newWindow.isOutsideTouchable = true
        newWindow.update()
        newWindow.showAtLocation(parent, Gravity.CENTER_VERTICAL, 0, 0)
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
            type = User.NORMAL_USER
            normaltext.setBackgroundResource(R.drawable.button_bg)
            supertext.background = null
        }
        addUser.setOnClickListener {
            val stringName: String = userName.text.toString()
            val stringPassword: String = userPassword.text.toString()
            addUser(stringName, stringPassword, type)
            initData()
        }
    }
}
