package com.example.omegatracker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.R
import com.example.omegatracker.entity.NavigationData
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.timer.TimerActivity
import com.example.omegatracker.utils.formatTimeDifference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Singleton



@Singleton
class TasksService : Service() {

    private lateinit var notificationManager: NotificationManager

    private var notificationBuilders = mutableMapOf<Int, NotificationCompat.Builder>()
    private var notifications = mutableMapOf<Int, Notification>()

    private val tasksManager = OmegaTrackerApplication.appComponent.tasksManager()

    inner class Controller : Binder(), IController {
        override fun startTask(taskRun: TaskRun) {
            tasksManager.launchTaskRunner(taskRun)
            createNotification(taskRun)
        }

        override fun getUpdatedTask(taskId: String): Flow<TaskRun> {
            taskUpdates(tasksManager.getTaskUpdates(taskId))
            return tasksManager.getTaskUpdates(taskId)
        }

        override fun stopUntilTimeTask(taskRun: TaskRun) {
            tasksManager.pauseTaskRunner(taskRun)
            stopNotificationTask(taskRun)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return Controller()
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        createNotificationGroup("TasksGroup")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_ALL_TASKS") {
            tasksManager.pauseAllTasksRunners()
            stopAllNotifications()
        }
        return START_STICKY
    }

    private fun createNotificationGroup(groupId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val groupChannel = NotificationChannel(
                groupId,
                "Tasks Group Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(groupChannel)
        }
    }

    private fun createGroupNotification(groupId: String): Notification {
        val stopAllIntent = Intent(this, TasksService::class.java).apply {
            action = "STOP_ALL_TASKS"
        }
        val stopAllPendingIntent = PendingIntent.getService(
            this,
            0,
            stopAllIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, groupId)
            .setSmallIcon(R.drawable.icon_monitor_circle)
            .setGroup(groupId)
            .addAction(R.drawable.stop_timer, "Stop All", stopAllPendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                NAME_CHANNEL,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                vibrationPattern = LongArray(0) { 0 }
                enableVibration(true)
                enableLights(false)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationIntent(taskRun: TaskRun): PendingIntent {
        val navigationData = NavigationData(Screens.TimerScreen, taskRun.id)
        return Intent(this, TimerActivity::class.java).apply {
            putExtra("navigation_data", navigationData)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let { intent ->
            PendingIntent.getActivity(
                this,
                taskRun.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    private fun setSettingNotification(taskRun: TaskRun) {
        val notificationId = taskRun.id.hashCode()
        val notificationIntent = createNotificationIntent(taskRun)

        val notificationBuilder = notificationBuilders[notificationId] ?: NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle("Задача ${taskRun.name}")
            setContentText(formatTimeDifference(taskRun.requiredTime, taskRun.fullTime))
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setOngoing(true)
            setContentIntent(notificationIntent)
            setSmallIcon(R.drawable.icon_monitor_circle)
            setGroup("TasksGroup")
        }

        notificationBuilders[notificationId] = notificationBuilder
        notifications[notificationId] = notificationBuilder.build()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun startForeground(taskRun: TaskRun) {
        val notificationId = taskRun.id.hashCode()
        startForeground(notificationId, notifications[notificationId])
    }

    fun taskUpdates(flow: Flow<TaskRun>) {
        CoroutineScope(Dispatchers.IO).launch {
            flow.collect { task ->
                updateTimeNotification(task)
            }
        }
    }

    private fun createNotification(taskRun: TaskRun) {
        setSettingNotification(taskRun)
        startForeground(taskRun)
        val groupId = "TasksGroup"
        notificationManager.notify(groupId.hashCode(), createGroupNotification(groupId))
    }

    private fun stopNotificationTask(taskRun: TaskRun) {
        val id = taskRun.id.hashCode()
        notificationBuilders.remove(id)
        notifications.remove(id)
        notificationManager.cancel(id)
    }

    private fun stopAllNotifications() {
        notificationManager.cancelAll()
        notificationBuilders.clear()
        notifications.clear()
        stopForeground(STOP_FOREGROUND_DETACH)
    }

    private fun updateTimeNotification(taskRun: TaskRun) {
        val notificationId = taskRun.id.hashCode()
        val notificationBuilder = notificationBuilders[notificationId]
        if (notificationBuilder != null) {
            val updatedText = formatTimeDifference(taskRun.requiredTime, taskRun.fullTime)
            notificationBuilder.setContentText(updatedText)
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }

    companion object {
        private const val CHANNEL_ID = "TasksServiceChannel"
        private const val NAME_CHANNEL = "TasksService"
    }
}