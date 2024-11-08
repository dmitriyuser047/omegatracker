package com.example.omegatracker.ui.start

import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.activity.BasePresenter

class StartPresenter() : BasePresenter<StartView>() {

    private val component = OmegaTrackerApplication.appComponent

    init {
        if (component.userManager().getUser() == null) {
            viewState.navigateScreen(Screens.AuthScreen)
        } else {
            viewState.navigateScreen(Screens.TasksScreen)
        }
    }

}