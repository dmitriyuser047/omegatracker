package com.example.omegatracker.data

import androidx.paging.PagingData
import com.example.omegatracker.db.entity.HistoryData
import com.example.omegatracker.db.entity.HistoryTask
import com.example.omegatracker.entity.HistoryItem
import com.example.omegatracker.entity.User
import com.example.omegatracker.entity.task.TaskFromJson
import com.example.omegatracker.entity.task.TaskRun
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface Repository {
    suspend fun getUser(token: String, clientUrl: String): User
    suspend fun getTasks(): Flow<List<TaskRun>>
    suspend fun insertTasksToBase(tasks: List<TaskRun>)
    suspend fun updateTasksBase(taskRuns: List<TaskRun>)
    suspend fun getTasksFlowFromDatabase(): Flow<List<TaskRun>>
    suspend fun getTasksFromDatabase(): List<TaskRun>
    suspend fun convertingTasks(tasksFromJson: List<TaskFromJson>): List<TaskRun>
    fun differenceCheckTaskRun(taskRun: TaskRun): Flow<TaskRun>
    suspend fun convertingTask(tasksFromJson: TaskFromJson, taskRun: TaskRun): TaskRun
    suspend fun updateTask(taskRun: TaskRun)
    suspend fun getTaskById(taskId: String): TaskRun?
    fun isToday(timestamp: Long): Boolean
    suspend fun addNewDataTaskToBase(task: TaskRun)
    suspend fun deleteData()
    fun getHistoryData(pageSize: Int):  Flow<PagingData<HistoryItem>>
    suspend fun completeTask(taskRun: TaskRun)
    suspend fun deleteTask(taskRun: TaskRun)
    suspend fun getAllHistoryTasks(): List<HistoryTask>
    fun getDayOfWeek(date: Date): String
    suspend fun getDataBetweenDate(time: Pair<Long, Long>): List<HistoryData>
    suspend fun clearHistoryItems()

//    suspend fun getImageUrlForTask(imageUrl: String?): String?
}