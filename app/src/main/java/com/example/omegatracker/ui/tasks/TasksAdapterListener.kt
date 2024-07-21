package com.example.omegatracker.ui.tasks

import com.example.omegatracker.entity.TaskRun

interface TasksAdapterListener {
    fun updateListTasks(list: List<TaskRun>, position: Int): List<TaskRun>
    fun updateTimeTasks(list: List<TaskRun>, taskRun: TaskRun): List<TaskRun>
    fun clickToTimer(taskRun: TaskRun, list: List<TaskRun>)
}