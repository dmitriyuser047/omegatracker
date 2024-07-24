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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Singleton


@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class TasksService: Service() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel

    private val tasksManager = OmegaTrackerApplication.appComponent.tasksManager()

    inner class Controller : Binder(), IController {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun startTask(taskRun: TaskRun) {
            tasksManager.launchTaskRunner(taskRun)
            subscribeToTaskUpdates(taskRun)
            startForegroundService(taskRun) // Запускаем foreground-сервис
        }

        override fun getUpdatedTask(taskRun: TaskRun): Flow<TaskRun> {
            return tasksManager.getTaskUpdates(taskRun.id)
        }

        override fun stopUntilTimeTask(taskRun: TaskRun) {
            tasksManager.stopUntilTimeTaskRunner(taskRun)
        }

        override fun handleCompletedTask(taskRun: TaskRun) {

        }

        override fun serviceDisconnect() {

        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return Controller()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        notificationChannel = NotificationChannel(CHANNEL_ID, NAME_CHANNEL, importance)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun createNotification(task: TaskRun, isForeground: Boolean): Notification {
        val intent = Intent(applicationContext, TasksActivity::class.java).apply {
            putExtra("taskId", task.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0, // requestCode
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannel.id)
            .setContentTitle(task.name)
            .setSmallIcon(R.drawable.icon_monitor_circle)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(isForeground)
            .setSound(null)
            .setVibrate(null)
            .setOnlyAlertOnce(true)

        if (isForeground) {
            notificationBuilder.setContentText("Task is running")
        } else {
            val notificationContentText = formatTimeDifference(task.requiredTime, task.fullTime)
            notificationBuilder.setContentText(notificationContentText)
        }

        return notificationBuilder.build()
    }
    private fun startForegroundService(taskRun: TaskRun) {
        val notification = createNotification(taskRun, isForeground = true)
        startForeground(taskRun.id.hashCode(), notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(task: TaskRun) {
        val notification = createNotification(task, isForeground = false)
        notificationManager.notify(task.id.hashCode(), notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun subscribeToTaskUpdates(taskRun: TaskRun) {
        CoroutineScope(Dispatchers.Default).launch {
            tasksManager.getTaskUpdates(taskRun.id).collect { update ->
                    showNotification(update)
            }
        }
    }

    companion object {
        private const val CHANNEL_ID = "TasksServiceChannel"
        private const val NAME_CHANNEL = "TasksService"
    }
}