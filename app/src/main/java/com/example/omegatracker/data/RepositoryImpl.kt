package com.example.omegatracker.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.db.entity.TaskData
import com.example.omegatracker.entity.task.State
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.entity.User
import com.example.omegatracker.entity.task.TaskFromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Singleton
class RepositoryImpl : Repository {

    private var youTrackApi: YouTrackApi = OmegaTrackerApplication.retrofitComponent.youTrackApi()

    private val component = OmegaTrackerApplication.appComponent

    private val taskDao = OmegaTrackerApplication.taskDataBase.taskDao()

    private var userToken: String? = null

    override suspend fun getUser(token: String, clientUrl: String): User {
        OmegaTrackerApplication.retrofitChangeUrl(clientUrl)
        youTrackApi = OmegaTrackerApplication.retrofitComponent.youTrackApi()
        return youTrackApi.signIn("Bearer $token")
    }

    override suspend fun getTasks(): Flow<List<TaskRun>> = flow {
        userToken = component.userManager().getToken()
        println("Token: $userToken")

        val tasksFromDatabase = getTasksFromDatabase()
        emit(tasksFromDatabase)

        val tasksFromJson = youTrackApi.getTasks("Bearer $userToken")
        emit(convertingTasks(tasksFromJson))
        insertTasksToBase(convertingTasks(tasksFromJson))

    }.flowOn(Dispatchers.IO)


    override suspend fun convertingTasks(tasksFromJson: List<TaskFromJson>): List<TaskRun> {
        val taskFromBase = getTasksFromDatabase().associateBy { it.id }
        return tasksFromJson.map { task ->
            val existingTask = taskFromBase[task.id]
            TaskRun(
                id = task.id,
                startTime = Duration.ZERO,
                name = task.name,
                description = if (task.description != existingTask?.description) task.description else existingTask?.description,
                projectName = if (task.projectName != existingTask?.projectName) task.projectName else existingTask?.projectName,
                state = existingTask?.state ?: task.state,
                workedTime = existingTask?.workedTime ?: task.workedTime,
                requiredTime = if (task.requiredTime != existingTask?.requiredTime) task.requiredTime else existingTask.requiredTime,
                isRunning = existingTask?.isRunning ?: false,
                spentTime = existingTask?.spentTime ?: Duration.ZERO,
                fullTime = existingTask?.fullTime ?: task.workedTime,
                dataCreate = existingTask?.dataCreate ?: task.dataCreate
            )
        }
    }

    override suspend fun convertingTask(tasksFromJson: TaskFromJson, taskRun: TaskRun): TaskRun {
        return TaskRun(
                id = tasksFromJson.id,
                startTime = taskRun.startTime,
                name = tasksFromJson.name,
                description = tasksFromJson.description,
                projectName = tasksFromJson.projectName,
                state = tasksFromJson.state,
                workedTime = tasksFromJson.workedTime,
                requiredTime = tasksFromJson.requiredTime,
                isRunning = taskRun.isRunning,
                spentTime =  taskRun.spentTime,
                fullTime = taskRun.fullTime,
                dataCreate = tasksFromJson.dataCreate
        )
    }

    override suspend fun updateTask(taskRun: TaskRun) {
        taskDao.upsertTasks(
            TaskData(
                id = taskRun.id,
                description = taskRun.description,
                name = taskRun.name,
                projectName = taskRun.projectName,
                state = (taskRun.state ?: State.Open).toString(),
                workedTimeLong = taskRun.workedTime.inWholeMinutes,
                requiredTimeLong = taskRun.requiredTime.inWholeMinutes,
                isRunning = taskRun.isRunning,
                startTimeLong = taskRun.startTime.toLong(DurationUnit.MILLISECONDS),
                endTimeLong = (taskRun.startTime + taskRun.spentTime).toLong(DurationUnit.MILLISECONDS),
                dataCreate = taskRun.dataCreate
            )
        )
    }

    override suspend fun getTaskById(taskId: String): TaskRun? {
        val foundTask = taskDao.findTaskById(taskId)?: return null
        return TaskRun(
                id = foundTask.id,
                startTime = foundTask.startTimeLong.toDuration(DurationUnit.MILLISECONDS),
                name = foundTask.name,
                description = foundTask.description,
                projectName = foundTask.projectName,
                state = State.Open.toString(),
                workedTime = foundTask.workedTime,
                requiredTime = foundTask.requiredTime,
                isRunning = foundTask.isRunning,
                spentTime = Duration.ZERO,
                fullTime = foundTask.workedTime,
                dataCreate = foundTask.dataCreate
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun isToday(timestamp: Long): Boolean {
        val instant = Instant.fromEpochMilliseconds(timestamp)

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        return dateTime.date == now.date
    }


    override suspend fun insertTasksToBase(tasks: List<TaskRun>) {
        tasks.forEach { task ->
                taskDao.upsertTasks(
                    TaskData(
                        id = task.id,
                        description = task.description,
                        name = task.name,
                        projectName = task.projectName,
                        state = task.state ?: State.Open.toString(),
                        workedTimeLong = task.workedTime.inWholeMinutes,
                        requiredTimeLong = task.requiredTime.inWholeMinutes,
                        isRunning = task.isRunning,
                        startTimeLong = task.startTime.toLong(DurationUnit.MILLISECONDS),
                        endTimeLong = task.startTime.toLong(DurationUnit.MILLISECONDS) + task.spentTime.toLong(DurationUnit.MILLISECONDS),
                        dataCreate = task.dataCreate
                        )
                )
        }
    }

    override suspend fun updateTasksBase(taskRuns: List<TaskRun>) {
        taskRuns.forEach { taskRun ->
            taskDao.upsertTasks(
                TaskData(
                    id = taskRun.id,
                    description = taskRun.description,
                    name = taskRun.name,
                    projectName = taskRun.projectName,
                    state = taskRun.state ?: State.Open.toString(),
                    workedTimeLong = taskRun.workedTime.inWholeMinutes,
                    requiredTimeLong = taskRun.requiredTime.inWholeMinutes,
                    isRunning = taskRun.isRunning,
                    startTimeLong = taskRun.startTime.toLong(DurationUnit.MILLISECONDS),
                    endTimeLong = (taskRun.startTime + taskRun.spentTime).toLong(DurationUnit.MILLISECONDS),
                    dataCreate = taskRun.dataCreate
                )
            )
        }
    }

    override suspend fun getTasksFromDatabase(): List<TaskRun> {
        val tasks = taskDao.getAllTasks()
        return tasks.map { task ->
            TaskRun(
                id = task.id,
                startTime = task.startTimeLong.toDuration(DurationUnit.MILLISECONDS),
                name = task.name,
                description = task.description,
                projectName = task.projectName,
                state = State.Open.toString(),
                workedTime = task.workedTime,
                requiredTime = task.requiredTime,
                isRunning = task.isRunning,
                spentTime = Duration.ZERO,
                fullTime = task.workedTime + (task.endTimeLong.toDuration(DurationUnit.MILLISECONDS) - task.startTimeLong.toDuration(DurationUnit.MILLISECONDS)),
                dataCreate = task.dataCreate
            )
        }
    }

    override fun differenceCheckTaskRun(taskRun: TaskRun): Flow<TaskRun> = flow {
        emit(taskRun)
        val taskUpdated = youTrackApi.readTask(taskRun.id,"Bearer $userToken")
        emit(convertingTask(taskUpdated, taskRun))
    }



}
