package com.example.omegatracker.ui.auth

import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.data.RepositoryImpl
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.BasePresenter
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthPresenter @Inject constructor(private val repositoryImpl: RepositoryImpl) :
    BasePresenter<AuthView>() {
        private val component = OmegaTrackerApplication.appComponent
    fun onSignInClicked(token: String, url: String) {
        launch {
            val result = repositoryImpl.getUser(token, url)
            component.userManager().setUser(token, result.id, result.avatarUrl, url)
            viewState.navigateTo(Screens.TasksScreen)
        }
    }
}