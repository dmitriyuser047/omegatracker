package com.example.omegatracker.ui.start

import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.BasePresenter

class StartPresenter : BasePresenter<StartView>() {
    private val component = OmegaTrackerApplication.appComponent
    private val clientUrl = component.userManager().getUserUrl()
    init {
        if (component.userManager().getUser() == null) {
            viewState.navigateTo(Screens.AuthScreen)
        } else {
            viewState.navigateTo(Screens.TasksScreen)
        }
    }
}