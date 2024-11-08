package com.example.omegatracker.ui.base.fragment

import com.example.omegatracker.ui.base.activity.BasePresenter
import com.example.omegatracker.ui.base.activity.BaseView
import com.omega_r.base.components.OmegaFragment

abstract class BaseFragment : OmegaFragment(), BaseView {

    abstract override val presenter: BasePresenter<out BaseView>

    protected abstract val layoutRes: Int

}
