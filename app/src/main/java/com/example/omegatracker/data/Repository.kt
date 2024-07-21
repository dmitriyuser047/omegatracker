package com.example.omegatracker.data

import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.entity.User
import com.example.omegatracker.entity.task.TaskFromJson
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getUser(token: String, clientUrl: String): User
    suspend fun getTasks(): Flow<List<TaskRun>>
    suspend fun insertTasksToBase(taskFromJson: List<TaskFromJson>)
    suspend fun updateTasksBase(taskRuns: List<TaskRun>)
    suspend fun getTasksFromDatabase(): List<TaskRun>
    suspend fun convertingTasks(tasksFromJson: List<TaskFromJson>): List<TaskRun>
    fun differenceCheckTaskRun(taskRun: TaskRun): Flow<TaskRun>
    suspend fun convertingTask(tasksFromJson: TaskFromJson): TaskRun
    suspend fun updateTask(taskRun: TaskRun)
}