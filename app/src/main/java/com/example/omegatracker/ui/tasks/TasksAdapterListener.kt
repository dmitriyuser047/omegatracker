package com.example.omegatracker.ui.tasks

import com.example.omegatracker.entity.task.TaskRun

interface TasksAdapterListener {
    fun updateListTasks(list: List<TaskRun>): List<TaskRun>
    fun updateTimeTasks(list: List<TaskRun>, taskRun: TaskRun): List<TaskRun>
    fun clickToTimer(taskRun: TaskRun, list: List<TaskRun>)
    fun startTask(taskRun: TaskRun)
    fun filterTasksByDate(filter: TaskFilter, tasksRun: List<TaskRun>): List<TaskRun>
}