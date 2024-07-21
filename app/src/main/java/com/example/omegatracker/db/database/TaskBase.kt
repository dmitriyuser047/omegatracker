package com.example.omegatracker.db.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.omegatracker.db.dao.TaskDao
import com.example.omegatracker.db.entity.TaskData

@Database(entities = [TaskData::class], version = 1, exportSchema = false)
abstract class TaskBase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskBase? = null

        fun getDatabase(context: Context): TaskBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskBase::class.java,
                    "task_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}