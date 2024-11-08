package com.example.omegatracker.ui.auth

import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.data.RepositoryImpl
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.activity.BasePresenter
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthPresenter @Inject constructor(private val repositoryImpl: RepositoryImpl) :
    BasePresenter<AuthView>() {

    private val component = OmegaTrackerApplication.appComponent

    fun onSignInClicked(token: String, url: String) {
        println("onSignInClicked: $token, $url")
        launch {
            println("launch: $token")
            val result = repositoryImpl.getUser(token, url)
            println("getUser result: $result")
            if (result != null) {
                component.userManager().setUser(token, result.id, result.avatarUrl, url)
                println("User set successfully")
                viewState.navigateScreen(Screens.TasksScreen)
            } else {
                println("getUser returned null")
            }
        }
    }
}