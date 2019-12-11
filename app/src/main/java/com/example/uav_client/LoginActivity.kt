package com.example.uav_client

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.example.uav_client.Application.SysApplication
import com.example.uav_client.Data.Common.RequestBuildUtil
import com.example.uav_client.Data.Common.User
import com.example.uav_client.Prensenters.MainPresenter
import com.example.uav_client.Contracts.MainTaskDetailContract
import com.example.uav_client.Data.Common.ReceiveBody
import com.example.uav_client.Data.DataSS.Datastatic

class LoginActivity : AppCompatActivity(), MainTaskDetailContract.View {
    override fun error() {
        Toast.makeText(this, "连接错误，请检查网络连接是否正常", Toast.LENGTH_SHORT).show()
    }

    lateinit var userNameEdit: EditText
    lateinit var passwordEdit: EditText
    lateinit var loginText: TextView
    lateinit var cancelLoginText: TextView
    lateinit var loadingText: TextView
    lateinit var namepu: String
    lateinit var passwordpu: String
    lateinit var loginLayout: LinearLayout
    lateinit var bgLayout: LinearLayout
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var presenter: MainTaskDetailContract.Presenter
    lateinit var textView: TextView
    var id: Int = 0
    var string = "登陆中"

    override fun showList(dataList: ByteArray, requestCode: Int) {
        var s2 = String(dataList)
        var list = ReceiveBody.initialParse(s2,"|")
        if (list[0].toInt() == 1 && list.size >= 6) {
            SysApplication.user = User(list[1], list[2], list[3], list[4].toInt(), list[5])
            loginskip()
        } else {
            Toast.makeText(this, "账户或密码错误", Toast.LENGTH_SHORT).show()
        }
    }

    var handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (string.length) {
                1 -> {
//                    SysApplication.user = User(namepu, passwordpu, User.NORMAL_USER, 0, id)
                }
                3, 4, 5 -> string += "."
                6 -> string = string.substring(0, 3)
            }
            loadingText.text = string
        }
    }


    override fun release() {

    }

    private fun hideHeader() {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.parseColor("#FFFFFF")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        hideHeader()
        initView()
        initEvents()
    }

    private fun initEvents() {
        presenter = MainPresenter(this)
        loginText.setOnClickListener {
            if(Datastatic.IP == ""){
                Toast.makeText(this@LoginActivity,"Ip不能为空，请先在下方设置Ip",Toast.LENGTH_SHORT).show()
            }else{
                loadingText.text = "登陆中"
                val s: String = userNameEdit.text.trim().toString() + "|" + passwordEdit.text.trim()
                presenter.getData(s, RequestBuildUtil.USER_LOGIN)
            }
        }
        cancelLoginText.setOnClickListener {

        }
    }

    private fun loginskip() {
        editor.putString("name", userNameEdit.text.trim().toString())
        editor.putString("password", passwordEdit.text.trim().toString())
        editor.commit()
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    private fun initView() {
        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        userNameEdit = findViewById(R.id.user_edit)
        textView = findViewById(R.id.ip_text)
        loginLayout = findViewById(R.id.login_lay)
        bgLayout = findViewById(R.id.login_bg)
        passwordEdit = findViewById(R.id.password_edit)
        loginText = findViewById(R.id.main_btn_login)
        cancelLoginText = findViewById(R.id.main_btn_cancel)
        loadingText = findViewById(R.id.load_text)
        var name = sharedPreferences.getString("name", "")
        var password = sharedPreferences.getString("password", "")
        var ip = sharedPreferences.getString("ip", "")
        Datastatic.IP = ip
        userNameEdit.setText(name)
        passwordEdit.setText(password)
        var s = Datastatic.IP
        textView.text = "Ip：$s"
        textView.setOnClickListener {
            bgLayout.visibility = View.VISIBLE
            popWindow(loginLayout)
        }
    }

    private fun popWindow(parent: ViewGroup) {
        val popupView = this@LoginActivity.layoutInflater.inflate(R.layout.popwindow_login, null)
        popupView.setPadding(50, 0, 50, 0)
        var newWindow = PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        newWindow.width = resources.getDimension(R.dimen.dp_280).toInt()
        newWindow.animationStyle = R.style.popup_window_anim
        newWindow.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))
        newWindow.isFocusable = true
        newWindow.isOutsideTouchable = true
        newWindow.update()
        newWindow.showAtLocation(parent, Gravity.CENTER_VERTICAL, 0, -100)
        var affirm = popupView.findViewById<TextView>(R.id.affirm_button)
        var ipEditText = popupView.findViewById<EditText>(R.id.ip_edit)
        ipEditText.setText(Datastatic.IP)
        ipEditText.inputType = EditorInfo.TYPE_CLASS_PHONE
        affirm.setOnClickListener {
            Datastatic.IP = ipEditText.text.toString().trim()
            newWindow.dismiss()
            var s = Datastatic.IP
            textView.text = "IP：   $s"
            editor.putString("ip",Datastatic.IP)
            editor.commit()
        }
        newWindow.setOnDismissListener {
            bgLayout.visibility = View.GONE
        }
    }
}
