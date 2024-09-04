package com.example.omegatracker.ui

import android.os.Parcelable
import com.example.omegatracker.entity.TaskRun
import kotlinx.parcelize.Parcelize

sealed class Screens {
    data object StartScreen : Screens()
    data object AuthScreen: Screens()
    data object TasksScreen: Screens()
    @Parcelize
    data class TimerScreen(val taskRun: TaskRun): Screens(), Parcelable
}