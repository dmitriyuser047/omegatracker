package com.example.omegatracker.ui.auth

import android.os.Bundle
import android.widget.EditText
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.databinding.ActivityAuthBinding
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.BaseActivity

class AuthActivity : BaseActivity(), AuthView {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var clientToken: String
    private lateinit var clientUrl: String
    private lateinit var inputLogin: EditText
    private val appComponent = OmegaTrackerApplication.appComponent

    override val presenter: AuthPresenter by providePresenter {
        AuthPresenter(appComponent.repository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        inputLogin = binding.inputLogin
        binding.buttonSignIn.setOnClickListener {
            clientToken = inputLogin.text.toString()
            clientUrl = binding.inputUrl.text.toString()
            startSignIn()
        }
        binding.bottomInfo.setOnClickListener {
            showInstruction()
        }
    }

    override fun showInstruction() {
        appComponent.authFragment().show(supportFragmentManager, "tag")
    }

    override fun navigateTo(screens: Screens) {
        createIntent(this, screens)
    }

    override fun startSignIn() {
        presenter.onSignInClicked(clientToken, clientUrl)
    }

}


