package com.example.uav_client.Prensenters

import com.example.uav_client.Contracts.LoginContract
import com.example.uav_client.LoginActivity

class LoginPresenter : LoginContract.Presenter {
    lateinit var loginActivity: LoginActivity

    constructor(view: LoginContract.View) {
        loginActivity = view as LoginActivity
    }

    override fun login(name: String, password: String) {

    }
}