package com.example.omegatracker.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.omegatracker.db.entity.TaskData

@Dao
interface TaskDao {
    @Query("SELECT * FROM taskdata")
    fun getAllTasks(): List<TaskData>

    @Upsert
    suspend fun upsertTasks(taskData: TaskData)

    @Query ("SELECT * FROM taskdata WHERE id  = :taskId")
    suspend fun findTaskById(taskId: String): TaskData?

    @Delete
    suspend fun deleteTask(taskData: TaskData)

    @Query("SELECT * FROM taskdata WHERE isRunning = 'true'")
    suspend fun getRunningTasks(): List<TaskData>

    @Query("SELECT * FROM taskdata WHERE isRunning = 'false'")
    suspend fun getNotRunningTasks(): List<TaskData>


    @Query("SELECT * FROM taskdata WHERE projectName LIKE :projectName")
    suspend fun findTasksByProjectName(projectName: String): List<TaskData>

    @Query("SELECT * FROM taskdata WHERE name LIKE :taskName")
    suspend fun findTasksByName(taskName: String): List<TaskData>

    @Query("SELECT * FROM taskdata WHERE workedTime = 0")
    suspend fun getTasksWithNoWorkedTime(): List<TaskData>

    @Query("SELECT * FROM taskdata WHERE description IS NULL")
    suspend fun findTasksWithNoDescription(): List<TaskData>


}