package com.example.omegatracker.ui.tasks

import com.example.omegatracker.service.TasksService

interface TasksCallback {
    fun setServiceController(controller: TasksService.Controller)
}