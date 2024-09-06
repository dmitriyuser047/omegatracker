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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.InboxStyle
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.R
import com.example.omegatracker.entity.NotificationActions
import com.example.omegatracker.entity.NotificationActions.*
import com.example.omegatracker.entity.task.TaskRun
import com.example.omegatracker.ui.tasks.TasksActivity
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

        override fun pauseTask(taskRun: TaskRun) {
            tasksManager.pauseTaskRunner(taskRun)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return Controller()
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action?.let { NotificationActions.valueOf(it) }

        when (action) {
            PAUSE_ALL -> {
                tasksManager.pauseAllTasksRunners()
            }
            PLAY_ALL -> {
                tasksManager.playAllTasksRunners()
            }
            PAUSE_TASK -> {
               TODO()
            }
            PLAY_TASK -> {
                TODO()
            }
            null -> {

            }
        }
        return START_STICKY
    }
    private fun createActionIntent(action: NotificationActions): PendingIntent {
        return Intent(this, TasksService::class.java).apply {
            this.action = action.name
        }.let { intent ->
            PendingIntent.getService(
                this,
                action.ordinal,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    private fun createNotificationChannel() {
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

    private fun createNotificationIntent(taskRun: TaskRun): PendingIntent {
        return Intent(this, TimerActivity::class.java).apply {
            putExtra("taskRun", taskRun.id)
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

    private fun createHeadNotificationIntent(): PendingIntent {
        val intent = Intent(this, TasksActivity::class.java)
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun setSettingNotification(taskRun: TaskRun) {
        val notificationId = taskRun.id.hashCode()
        val notificationIntent = createNotificationIntent(taskRun)
        val pauseIntent = createActionIntent(PAUSE_TASK)
        val playIntent = createActionIntent(PLAY_TASK)

        val notificationBuilder = notificationBuilders[notificationId] ?: NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle("Задача ${taskRun.name}")
            setContentText(formatTimeDifference(taskRun.requiredTime, taskRun.fullTime))
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setOngoing(true)
            setContentIntent(notificationIntent)
            setSmallIcon(R.drawable.icon_monitor_circle)
            setGroup(GROUP_ID)
            addAction(R.drawable.pause_notification, "Pause", pauseIntent)
            addAction(R.drawable.play, "Play", playIntent)
    }

        notificationBuilders[notificationId] = notificationBuilder
        notifications[notificationId] = notificationBuilder.build()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun setHeadNotification() {
        val headNotificationId = 0
        val headNotificationIntent = createHeadNotificationIntent()

        val headNotificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle("Your Tasks")
            setContentText("You have active tasks")
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setOngoing(true)
            setContentIntent(headNotificationIntent)
            setSmallIcon(R.drawable.icon_monitor_circle)
            setGroup(GROUP_ID)
            setGroupSummary(true)

            val inboxStyle = NotificationCompat.InboxStyle()
            inboxStyle.addLine("Active tasks summary")
            setStyle(inboxStyle)

            val pauseAllIntent = createActionIntent(PAUSE_ALL)
            val playAllIntent = createActionIntent(PLAY_ALL)
            addAction(R.drawable.pause_notification, "Pause All", pauseAllIntent)
            addAction(R.drawable.play, "Play All", playAllIntent)
        }

        notificationManager.notify(headNotificationId, headNotificationBuilder.build())
    }

    private fun createNotification(taskRun: TaskRun) {
        setHeadNotification()
        setSettingNotification(taskRun)
        startForeground(taskRun)
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

    private fun updateTimeNotification(taskRun: TaskRun) {
        val notificationId = taskRun.id.hashCode()
        val notificationBuilder = notificationBuilders[notificationId]
        if (notificationBuilder != null) {
            val updatedText = formatTimeDifference(taskRun.requiredTime, taskRun.fullTime)
            notificationBuilder.setContentText(updatedText)
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }

//    private fun stopNotificationTask(taskRun: TaskRun) {
//        val id = taskRun.id.hashCode()
//        notificationBuilders.remove(id)
//        notifications.remove(id)
//        notificationManager.cancel(id)
//    }
//
//    private fun stopAllNotifications() {
//        notificationManager.cancelAll()
//        notificationBuilders.clear()
//        notifications.clear()
//        stopForeground(STOP_FOREGROUND_DETACH)
//    }

    companion object {
        private const val CHANNEL_ID = "TasksServiceChannel"
        private const val NAME_CHANNEL = "TasksService"
        private const val GROUP_ID = "TasksGroup"
    }
}