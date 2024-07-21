package com.example.omegatracker.ui

sealed class Screens {
    data object AuthScreen : Screens()
    data object StartScreen : Screens()
    data object TasksScreen : Screens()
}