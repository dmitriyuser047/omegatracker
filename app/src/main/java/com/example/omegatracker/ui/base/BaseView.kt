package com.example.omegatracker.ui.base

import com.example.omegatracker.entity.NavigationData
import com.omega_r.base.mvp.views.OmegaView

interface BaseView : OmegaView {
    fun showScreen(navigationData: NavigationData)
}