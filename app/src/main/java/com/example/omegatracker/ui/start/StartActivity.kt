package com.example.omegatracker.ui.start

import android.os.Bundle
import com.example.omegatracker.R
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.activity.BaseActivity


class StartActivity : BaseActivity(), StartView {

    override val presenter: StartPresenter by providePresenter {
        StartPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }


    override fun navigateScreen(screens: Screens) {
        startScreen(this, screens)
    }

}