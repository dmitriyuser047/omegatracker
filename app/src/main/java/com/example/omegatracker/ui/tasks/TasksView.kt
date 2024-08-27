package com.example.omegatracker.ui.tasks

import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.ui.base.BaseView

interface TasksView : BaseView {
    fun showTasks(taskData: List<TaskRun>)
    fun showIconProfile()
    fun startTaskTime(task: TaskRun)
    fun showUserSettings()
    fun exitProfile()
    fun setNewTasksTime(task: TaskRun)
    fun initialization()
}