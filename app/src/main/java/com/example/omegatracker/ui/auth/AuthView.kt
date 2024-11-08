package com.example.omegatracker.ui.auth

import com.example.omegatracker.ui.base.activity.BaseView

interface AuthView : BaseView {
    fun startSignIn()
    fun showInstruction()
}