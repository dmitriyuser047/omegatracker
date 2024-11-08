package com.example.omegatracker.ui.main

import com.example.omegatracker.entity.task.TaskRun

interface AddCustomTaskListener {
    fun onDialogDismiss()
    fun onTaskAdded(task: TaskRun)
}