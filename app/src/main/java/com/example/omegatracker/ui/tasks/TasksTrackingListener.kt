package com.example.omegatracker.ui.tasks

import kotlin.time.Duration

interface TasksTrackingListener {
    fun onDialogDismiss(timeLimit: Duration)
}