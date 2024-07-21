package com.example.omegatracker.data

import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.db.entity.TaskData
import com.example.omegatracker.entity.State
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.entity.User
import com.example.omegatracker.entity.task.TaskFromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
        insertTasksToBase(tasksFromJson)

    }.flowOn(Dispatchers.IO)


    override suspend fun convertingTasks(tasksFromJson: List<TaskFromJson>): List<TaskRun> {
        return tasksFromJson.map { task ->
            TaskRun(
                id = task.id,
                startTime = Duration.ZERO,
                name = task.name,
                description = task.description,
                projectName = task.projectName,
                state = task.state,
                workedTime = task.workedTime,
                requiredTime = task.requiredTime,
                isRunning = task.isRunning,
                spentTime = Duration.ZERO,
                fullTime = task.workedTime
            )
        }
    }

    override suspend fun convertingTask(tasksFromJson: TaskFromJson): TaskRun {
        return TaskRun(
                id = tasksFromJson.id,
                startTime = Duration.ZERO,
                name = tasksFromJson.name,
                description = tasksFromJson.description,
                projectName = tasksFromJson.projectName,
                state = tasksFromJson.state,
                workedTime = tasksFromJson.workedTime,
                requiredTime = tasksFromJson.requiredTime,
                isRunning = tasksFromJson.isRunning,
                spentTime =  Duration.ZERO,
                fullTime = tasksFromJson.workedTime
        )
    }

    override suspend fun updateTask(taskRun: TaskRun) {
        taskDao.upsertTasks(
            TaskData(
                id = taskRun.id,
                description = taskRun.description,
                name = taskRun.name,
                projectName = taskRun.projectName,
                state = State.Open.toString(),
                workedTimeLong = taskRun.workedTime.inWholeMinutes,
                requiredTimeLong = taskRun.requiredTime.inWholeMinutes,
                isRunning = taskRun.isRunning,
                startTimeLong = taskRun.startTime.toLong(DurationUnit.MILLISECONDS),
                endTimeLong = (taskRun.startTime + taskRun.spentTime).toLong(DurationUnit.MILLISECONDS)
            )
        )
    }


    override suspend fun insertTasksToBase(tasksFromJson: List<TaskFromJson>) {
        tasksFromJson.forEach { taskFromJson ->
                taskDao.upsertTasks(
                    TaskData(
                        id = taskFromJson.id,
                        description = taskFromJson.description,
                        name = taskFromJson.name,
                        projectName = taskFromJson.projectName,
                        state = State.Open.toString(),
                        workedTimeLong = taskFromJson.workedTime.inWholeMinutes,
                        requiredTimeLong = taskFromJson.requiredTime.inWholeMinutes,
                        isRunning = taskFromJson.isRunning,
                        startTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
                        endTimeLong = Duration.ZERO.toLong(DurationUnit.MINUTES),
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
                )
            )
        }
    }

    override suspend fun getTasksFromDatabase(): List<TaskRun> {
        val tasks = taskDao.getAllTasks()
        return tasks.map { task ->
            TaskRun(
                id = task.id,
                startTime = Duration.ZERO,
                name = task.name,
                description = task.description,
                projectName = task.projectName,
                state = State.Open.toString(),
                workedTime = task.workedTime,
                requiredTime = task.requiredTime,
                isRunning = task.isRunning,
                spentTime = task.endTimeLong.toDuration(DurationUnit.MILLISECONDS) - task.startTimeLong.toDuration(DurationUnit.MILLISECONDS),
                fullTime = task.workedTime + (task.endTimeLong.toDuration(DurationUnit.MILLISECONDS) - task.startTimeLong.toDuration(DurationUnit.MILLISECONDS))
            )
        }
    }

    override fun differenceCheckTaskRun(taskRun: TaskRun): Flow<TaskRun> {
        return flow {
            emit(taskRun)
            val taskUpdated = youTrackApi.readTask(taskRun.id,"Bearer $userToken")
            emit(convertingTask(taskUpdated))
        }
    }

}
