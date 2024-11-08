package com.example.omegatracker.ui.base.activity

import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.R
import com.omega_r.base.mvp.presenters.OmegaPresenter
import com.omega_r.libs.omegatypes.toText
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.HttpException
import java.net.UnknownHostException

open class BasePresenter<T : BaseView> : OmegaPresenter<T>(), CoroutineScope {

    private val supervisorJob = SupervisorJob()
    private val stringResources = OmegaTrackerApplication.appComponent.context().resources

    private val coroutineException =
        CoroutineExceptionHandler { _, throwable ->
            when (throwable) {
                is UnknownHostException -> {
                    viewState.showToast(stringResources.getString(R.string.state_network).toText())
                }

                is HttpException -> {
                    viewState.showToast(stringResources.getString(R.string.error_user).toText())
                }

                is Exception -> {
                    throwable.printStackTrace()
                    viewState.showToast("Что-то пошло не так".toText())
                }
            }
        }

    override val coroutineContext = supervisorJob + Dispatchers.Main  + coroutineException
}