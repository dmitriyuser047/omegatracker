package com.example.omegatracker.ui.auth

import com.example.omegatracker.ui.base.BaseView

interface AuthView : BaseView {
    fun startSignIn()
    fun showInstruction()
}