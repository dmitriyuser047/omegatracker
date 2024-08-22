package com.example.omegatracker.ui.base

import android.os.Bundle
import com.example.omegatracker.ui.Screens
import com.omega_r.base.mvp.views.OmegaView

interface BaseView : OmegaView {
    fun showScreen(screen: Screens, extras: Bundle?)
}