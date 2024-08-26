package com.example.omegatracker.ui.start

import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.entity.NavigationData
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.BasePresenter

class StartPresenter : BasePresenter<StartView>() {
    private val component = OmegaTrackerApplication.appComponent
    private val clientUrl = component.userManager().getUserUrl()
    init {
        if (component.userManager().getUser() == null) {
            viewState.showScreen(NavigationData(Screens.AuthScreen,null))
        } else {
            viewState.showScreen(NavigationData(Screens.TasksScreen,null))
        }
    }
}