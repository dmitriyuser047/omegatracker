package com.example.omegatracker.data

import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.db.entity.HistoryTask
import com.example.omegatracker.db.entity.TaskData
import com.example.omegatracker.entity.HistoryItem
import com.example.omegatracker.entity.User
import com.example.omegatracker.entity.task.State
import com.example.omegatracker.entity.task.TaskFromJson
import com.example.omegatracker.entity.task.TaskRun
import com.example.omegatracker.utils.getCurrentDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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

    override suspend fun getTasks(): Flow<List<TaskRun>> = flow{
        userToken = component.userManager().getToken()
        println("Token: $userToken")

        val tasksFromDatabaseFlow = getTasksFlowFromDatabase()

        val tasksFromJson = youTrackApi.getTasks("Bearer $userToken")
        val convertedTasks = convertingTasks(tasksFromJson)

        insertTasksToBase(convertedTasks)

        tasksFromDatabaseFlow.collect { tasksFromDatabase ->
            emit(tasksFromDatabase)
        }

    }.flowOn(Dispatchers.IO)

    override suspend fun convertingTasks(tasksFromJson: List<TaskFromJson>): List<TaskRun> {
        val taskFromBase = getTasksFlowFromDatabase().first().associateBy { it.id }
        return tasksFromJson.map { task ->
            val existingTask = taskFromBase[task.id]
            TaskRun(
                id = task.id,
                startTime = existingTask?.startTime ?: Duration.ZERO,
                name = task.name,
                description = if (task.description != existingTask?.description) task.description else existingTask?.description,
                projectName = if (task.projectName != existingTask?.projectName) task.projectName else existingTask?.projectName,
                state = (if (existingTask?.isRunning == true || task.state == State.InProgress.toString()) State.InProgress else State.Open).toString(),
                workedTime = existingTask?.workedTime ?: task.workedTime,
                requiredTime = if (task.requiredTime != existingTask?.requiredTime) task.requiredTime else existingTask.requiredTime,
                isRunning = existingTask?.isRunning ?: false,
                spentTime = existingTask?.spentTime ?: Duration.ZERO,
                fullTime = existingTask?.fullTime ?: task.workedTime,
                dataCreate = existingTask?.dataCreate ?: task.dataCreate,
                imageUrl = task.imageUrl
            )
        } + taskFromBase.filterKeys { key -> !tasksFromJson.any { it.id == key } }.values.map { existingTask ->
            TaskRun(
                id = existingTask.id,
                startTime = existingTask.startTime,
                name = existingTask.name,
                description = existingTask.description,
                projectName = existingTask.projectName,
                state = if (existingTask.isRunning == true) State.InProgress.toString() else State.Open.toString(),
                workedTime = existingTask.workedTime,
                requiredTime = existingTask.requiredTime,
                isRunning = existingTask.isRunning,
                spentTime = existingTask.spentTime,
                fullTime = existingTask.fullTime,
                dataCreate = existingTask.dataCreate,
                imageUrl = existingTask.imageUrl
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
            state = (if (taskRun.isRunning == true || tasksFromJson.state == State.InProgress.toString()) State.InProgress else State.Open).toString(),
            workedTime = tasksFromJson.workedTime,
            requiredTime = tasksFromJson.requiredTime,
            isRunning = taskRun.isRunning,
            spentTime = taskRun.spentTime,
            fullTime = taskRun.fullTime,
            dataCreate = tasksFromJson.dataCreate,
            imageUrl = tasksFromJson.imageUrl
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
                dataCreate = taskRun.dataCreate,
                imageUrl = taskRun.imageUrl
            )
        )
    }

    override suspend fun getTaskById(taskId: String): TaskRun? {
        val foundTask = taskDao.findTaskById(taskId) ?: return null
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
            spentTime = (foundTask.endTimeLong - foundTask.startTimeLong).toDuration(DurationUnit.MILLISECONDS),
            fullTime = foundTask.workedTime,
            dataCreate = foundTask.dataCreate,
            imageUrl = foundTask.imageUrl
        )
    }

    override fun isToday(timestamp: Long): Boolean {
        val instant = Instant.fromEpochMilliseconds(timestamp)

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        return dateTime.date == now.date
    }

    override suspend fun addNewDataTaskToBase(task: TaskRun) {
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
                endTimeLong = task.startTime.toLong(DurationUnit.MILLISECONDS) + task.spentTime.toLong(
                    DurationUnit.MILLISECONDS
                ),
                dataCreate = task.dataCreate,
                imageUrl = task.imageUrl
            )
        )
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
                    endTimeLong = task.startTime.toLong(DurationUnit.MILLISECONDS) + task.spentTime.toLong(
                        DurationUnit.MILLISECONDS
                    ),
                    dataCreate = task.dataCreate,
                    imageUrl = task.imageUrl
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
                    dataCreate = taskRun.dataCreate,
                    imageUrl = taskRun.imageUrl
                )
            )
        }
    }

    override suspend fun getTasksFlowFromDatabase(): Flow<List<TaskRun>> {
        return taskDao.getAllFlowTasks().map { tasks ->
            tasks.map { task ->
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
                    spentTime = (task.endTimeLong - task.startTimeLong).toDuration(DurationUnit.MILLISECONDS),
                    fullTime = task.workedTime + (task.endTimeLong.toDuration(DurationUnit.MILLISECONDS) - task.startTimeLong.toDuration(
                        DurationUnit.MILLISECONDS
                    )),
                    dataCreate = task.dataCreate,
                    imageUrl = task.imageUrl
                )
            }
        }
    }

    override suspend fun getTasksFromDatabase(): List<TaskRun> {
        return taskDao.getAllTasks().map { task ->
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
                    spentTime = (task.endTimeLong - task.startTimeLong).toDuration(DurationUnit.MILLISECONDS),
                    fullTime = task.workedTime + (task.endTimeLong.toDuration(DurationUnit.MILLISECONDS) - task.startTimeLong.toDuration(
                        DurationUnit.MILLISECONDS
                    )),
                    dataCreate = task.dataCreate,
                    imageUrl = task.imageUrl
                )
            }
    }

    override fun differenceCheckTaskRun(taskRun: TaskRun): Flow<TaskRun> = flow {
        emit(taskRun)
        val taskUpdated = youTrackApi.readTask(taskRun.id, "Bearer $userToken")
        emit(convertingTask(taskUpdated, taskRun))
    }

    override suspend fun deleteData() {
        taskDao.deleteAllTasks()
    }

    override suspend fun getHistoryTasks(): List<HistoryItem> {
        val historiesData = taskDao.getAllHistoryTask()
        println(historiesData.groupBy { it.taskData.endTimeLong })
        val historyItems = historiesData.flatMap { historyData ->
            val taskData = historyData.taskData
            historyData.historyTasks.map { historyTask ->
                HistoryItem(
                    historyTaskName = taskData.name,
                    historyTaskProject = taskData.projectName ?: "Нет проекта",
                    startTime = historyTask.startTime,
                    endTime = historyTask.endTime,
                    date = historyTask.date,
                    historyTaskId = historyTask.taskId
                )
            }
        }
        return historyItems
    }

    override suspend fun completeTask(taskRun: TaskRun) {
        taskDao.upsertHistoryTask(
            HistoryTask(
                taskId = taskRun.id,
                startTime = taskRun.startTime.toLong(DurationUnit.MILLISECONDS),
                endTime = taskRun.startTime.toLong(DurationUnit.MILLISECONDS),
                date = getCurrentDate()
            )
        )
    }

    override suspend fun deleteTask(taskRun: TaskRun) {
        taskDao.deleteTask(taskId = taskRun.id)
    }

//    override suspend fun getImageUrlForTask(imageUrl: String?): String? {
//        val baseUrl = component.userManager().getUserUrl()
//        println(baseUrl + imageUrl)
//        return youTrackApi.getImageForTask(baseUrl + imageUrl,"Bearer $userToken")
//
//    }

}
