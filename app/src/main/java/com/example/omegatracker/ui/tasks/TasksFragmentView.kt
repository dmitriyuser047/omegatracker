package com.example.omegatracker.ui.tasks

import com.example.omegatracker.entity.task.TaskRun
import com.example.omegatracker.ui.base.fragment.BaseFragmentView


interface TasksFragmentView : BaseFragmentView {
    fun setTasks(taskRuns: List<TaskRun>)
    fun setNewTasksTime(taskRun: TaskRun)
    fun exitProfile()
}