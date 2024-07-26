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
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.ui.tasks.TasksActivity
import com.example.omegatracker.utils.formatTimeDifference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.debounce
import kotlinx.coroutines.time.sample
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds


@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class TasksService : Service() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private val notifications = mutableMapOf<Int, Notification>()

    private val tasksManager = OmegaTrackerApplication.appComponent.tasksManager()

    inner class Controller : Binder(), IController {
        override fun startTask(taskRun: TaskRun) {
            tasksManager.launchTaskRunner(taskRun)
            startForegroundService(taskRun)
        }

        override fun getUpdatedTask(taskRun: TaskRun): Flow<TaskRun> {
            return tasksManager.getTaskUpdates(taskRun.id)
        }

        override fun stopUntilTimeTask(taskRun: TaskRun) {
            tasksManager.stopUntilTimeTaskRunner(taskRun)
        }

        override fun handleCompletedTask(taskRun: TaskRun) {
            // Implementation for handling completed task
        }

        override fun serviceDisconnect() {
            // Implementation for handling service disconnect
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
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        notificationChannel = NotificationChannel(CHANNEL_ID, NAME_CHANNEL, importance).apply {
            setSound(null, null)
            enableVibration(false)
        }
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun createNotificationIntent(task: TaskRun): PendingIntent {
        return Intent(applicationContext, TasksActivity::class.java).apply {
            putExtra("taskId", task.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let { intent ->
            PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    private fun createNotification(task: TaskRun): Notification {
        val existingNotification = notifications[task.id.hashCode()]
        if (existingNotification!= null) {
            return existingNotification
        }
        val pendingIntent = createNotificationIntent(task)
        println(formatTimeDifference(task.requiredTime, task.fullTime))
        val newNotification = NotificationCompat.Builder(this, notificationChannel.id)
            .setContentTitle(task.name)
            .setContentText(formatTimeDifference(task.requiredTime, task.fullTime))
            .setSmallIcon(R.drawable.icon_monitor_circle)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notifications[task.id.hashCode()] = newNotification
        return newNotification
    }

    private fun updateNotification(task: TaskRun) {
//        val notification = notifications[task.id.hashCode()].let {
////            NotificationCompat.Builder(this, notificationChannel.id)
////                .setContentText(formatTimeDifference(task.requiredTime, task.fullTime))
////                .build()
//        }
//        notificationManager.notify(task.id.hashCode(), notification)

    }

    private fun startForegroundService(taskRun: TaskRun) {
        val notification = notifications[taskRun.id.hashCode()]?: createNotification(taskRun)
        subscribeToTaskUpdates(taskRun)
        //startForeground(taskRun.id.hashCode(), notification)
    }
    private fun subscribeToTaskUpdates(task: TaskRun) {
        val delay = 3000.milliseconds
        CoroutineScope(Dispatchers.IO).launch {
            tasksManager.getTaskUpdates(task.id)
                .sample(delay)
                .collect {
                    updateNotification(it)
                }
        }
    }

    companion object {
        private const val CHANNEL_ID = "TasksServiceChannel"
        private const val NAME_CHANNEL = "TasksService"
    }
}