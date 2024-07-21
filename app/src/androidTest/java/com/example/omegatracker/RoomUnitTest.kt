package com.example.omegatracker

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.example.omegatracker.db.dao.TaskDao
import com.example.omegatracker.db.database.TaskBase
import com.example.omegatracker.db.entity.TaskData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.time.Duration
import kotlin.time.DurationUnit


@RunWith(AndroidJUnit4::class)
class RoomUnitTest {
    private lateinit var taskDao: TaskDao
    private lateinit var database: TaskBase

    @Before
    fun createDataBase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, TaskBase::class.java).build()
        taskDao = database.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDataBase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertInDataBase() {
        CoroutineScope(Dispatchers.IO).launch {
            val task = TaskData(
                id = "1",
                description = "123",
                name = "task1",
                projectName = "projectTask1",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = true,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            taskDao.upsertTasks(taskData = task)
            val foundTask = taskDao.findTaskById(task.id)
            assertNotNull(foundTask)
            assertEquals(task, foundTask)
        }
    }

    @Test
    @Throws(Exception::class)
    fun getTaskByIdFromDataBase() {
        CoroutineScope(Dispatchers.IO).launch {
            val task1 = TaskData(
                id = "1",
                description = "123",
                name = "task1",
                projectName = "projectTask1",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = true,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            val task2 = TaskData(
                id = "2",
                description = "1223",
                name = "task2",
                projectName = "projectTask2",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = false,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            taskDao.upsertTasks(task1)
            taskDao.upsertTasks(task2)
            val taskById = taskDao.findTaskById("1")
            assertNotNull(taskById)
            assertEquals(task1, taskById)
        }
    }

    @Test
    @Throws(Exception::class)
    fun getAllTasksFromDataBase() {
        CoroutineScope(Dispatchers.IO).launch {
            val task = TaskData(
                id = "1",
                description = "123",
                name = "task1",
                projectName = "projectTask1",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = true,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            val task2 = TaskData(
                id = "2",
                description = "1223",
                name = "task2",
                projectName = "projectTask2",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = false,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            taskDao.upsertTasks(task)
            taskDao.upsertTasks(task2)
            val foundTasks = taskDao.getAllTasks()
            assertNotNull(foundTasks)
            assertEquals(2, foundTasks.size)
        }
    }

    @Test
    @Throws(Exception::class)
    fun deleteTask() {
        CoroutineScope(Dispatchers.IO).launch {
            val task = TaskData(
                id = "1",
                description = "123",
                name = "task1",
                projectName = "projectTask1",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = true,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            taskDao.upsertTasks(taskData = task)
            taskDao.deleteTask(task)
            val foundTask = taskDao.findTaskById(task.id)
            assertNull(foundTask)
        }
    }

    @Test
    @Throws(Exception::class)
    fun getRunningTasksFromDataBase() {
        CoroutineScope(Dispatchers.IO).launch {
            val task1 = TaskData(
                id = "1",
                description = "123",
                name = "task1",
                projectName = "projectTask1",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = true,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            val task2 = TaskData(
                id = "2",
                description = "1223",
                name = "task2",
                projectName = "projectTask2",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = false,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            taskDao.upsertTasks(task1)
            taskDao.upsertTasks(task2)
            val isRunningTasks = taskDao.getRunningTasks()
            assertEquals(1, isRunningTasks.size)
            assertEquals("1", isRunningTasks[0].id)
        }
    }

    @Test
    @Throws(Exception::class)
    fun getNotRunningTaskFromDataBase() {
        CoroutineScope(Dispatchers.IO).launch {
            val task1 = TaskData(
                id = "1",
                description = "123",
                name = "task1",
                projectName = "projectTask1",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = true,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            val task2 = TaskData(
                id = "2",
                description = "1223",
                name = "task2",
                projectName = "projectTask2",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = false,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            taskDao.upsertTasks(task1)
            taskDao.upsertTasks(task2)
            val isRunningTasks = taskDao.getNotRunningTasks()
            assertEquals(1, isRunningTasks.size)
            assertEquals("2", isRunningTasks[1].id)
        }
    }

    @Test
    @Throws(Exception::class)
    fun getTaskByProjectFromDataBase() {
        CoroutineScope(Dispatchers.IO).launch {
            val task1 = TaskData(
                id = "1",
                description = "123",
                name = "task1",
                projectName = "projectTask1",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = true,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            val task2 = TaskData(
                id = "2",
                description = "1223",
                name = "task2",
                projectName = "projectTask2",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = false,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            taskDao.upsertTasks(task1)
            taskDao.upsertTasks(task2)
            val taskByProject = taskDao.findTasksByProjectName("projectTask1")
            assertEquals(1, taskByProject.size)
            assertEquals("1", taskByProject[0].id)
        }
    }

    @Test
    @Throws(Exception::class)
    fun getTaskByNameFromDataBase() {
        CoroutineScope(Dispatchers.IO).launch {
            val task1 = TaskData(
                id = "1",
                description = "123",
                name = "task1",
                projectName = "projectTask1",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = true,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            val task2 = TaskData(
                id = "2",
                description = "1223",
                name = "task2",
                projectName = "projectTask2",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = false,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            taskDao.upsertTasks(task1)
            taskDao.upsertTasks(task2)
            val taskByName = taskDao.findTasksByName("task1")
            assertEquals(1, taskByName.size)
            assertEquals("1", taskByName[0].id)
        }
    }

    @Test
    @Throws(Exception::class)
    fun getTaskByWorkedTimeFromDataBase() {
        CoroutineScope(Dispatchers.IO).launch {
            val task1 = TaskData(
                id = "1",
                description = "123",
                name = "task1",
                projectName = "projectTask1",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = true,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            val task2 = TaskData(
                id = "2",
                description = "1223",
                name = "task2",
                projectName = "projectTask2",
                state = "Open",
                workedTimeLong = 123,
                requiredTimeLong = 123,
                isRunning = false,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            taskDao.upsertTasks(task1)
            taskDao.upsertTasks(task2)
            val taskByWorkedTime = taskDao.getTasksWithNoWorkedTime()
            assertEquals(1, taskByWorkedTime.size)
            assertEquals("1", taskByWorkedTime[0].id)
        }
    }

    @Test
    @Throws(Exception::class)
    fun getTaskByDescriptionEmptyFromDataBase() {
        CoroutineScope(Dispatchers.IO).launch {
            val task1 = TaskData(
                id = "1",
                description = null,
                name = "task1",
                projectName = "projectTask1",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = true,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            val task2 = TaskData(
                id = "2",
                description = "1223",
                name = "task2",
                projectName = "projectTask2",
                state = "Open",
                workedTimeLong = 0,
                requiredTimeLong = 0,
                isRunning = false,
                startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
            )
            taskDao.upsertTasks(task1)
            taskDao.upsertTasks(task2)
            val taskByProject = taskDao.findTasksWithNoDescription()
            assertEquals(1, taskByProject.size)
            assertEquals("1", taskByProject[0].id)
        }
    }

}