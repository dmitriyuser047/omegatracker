package com.example.omegatracker.ui.main

import com.example.omegatracker.entity.Fragments
import com.example.omegatracker.ui.base.activity.BaseView

interface MainView : BaseView {
    fun initialization()
    fun addTaskButton()
    fun startFragment(fragment: Fragments, addBackStack: Boolean)
}