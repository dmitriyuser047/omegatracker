package com.example.omegatracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.omegatracker.db.entity.HistoryData
import com.example.omegatracker.db.entity.HistoryTask
import com.example.omegatracker.db.entity.TaskData
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM taskdata")
    fun getAllFlowTasks(): Flow<List<TaskData>>

    @Query("SELECT * FROM taskdata")
    suspend fun getAllTasks(): List<TaskData>

    @Upsert
    suspend fun upsertTasks(taskData: TaskData)

    @Query("SELECT * FROM taskdata WHERE id  = :taskId")
    suspend fun findTaskById(taskId: String): TaskData?

    @Query("DELETE FROM taskdata WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)

    @Query("DELETE FROM taskdata")
    suspend fun deleteAllTasks()

    @Transaction
    @Query("SELECT * FROM TaskData")
    suspend fun getAllHistoryTask(): List<HistoryData>

    @Insert
    suspend fun upsertHistoryTask(historyTask: HistoryTask)
}
