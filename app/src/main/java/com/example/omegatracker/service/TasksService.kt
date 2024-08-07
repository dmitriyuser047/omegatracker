package com.example.omegatracker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
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
import com.example.omegatracker.utils.formatTimeDifference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
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
            //startForeground(taskRun)
            //taskUpdates(tasksManager.getTaskUpdates(taskRun.id))
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

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                CHANNEL_ID,
                NAME_CHANNEL,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun setSettingNotification(taskRun: TaskRun) {
        val notificationId = taskRun.id.hashCode()

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Задача ${taskRun.name} ")
            .setContentText(formatTimeDifference(taskRun.requiredTime, taskRun.fullTime))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false)
            .setOngoing(true)
            .setVibrate(null)
            .setSilent(true)

        val notification = notificationBuilder.build()
        notifications[notificationId] = notification
    }

    private fun startForeground(taskRun: TaskRun) {
        setSettingNotification(taskRun)
        val notificationId = taskRun.id.hashCode()
        startForeground(notificationId, notifications[notificationId])
    }

    @OptIn(FlowPreview::class)
    fun taskUpdates(flow: Flow<TaskRun>) {
        CoroutineScope(Dispatchers.IO).launch {
            flow.sample(5000).collect { task ->
                updateTimeNotification(task)
            }
        }
    }

    private fun updateTimeNotification(taskRun: TaskRun) {
            val idTask = taskRun.id.hashCode()
            val notification = notifications[idTask]

            if (notification!= null) {
                val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Задача ${taskRun.name} ")
                    .setContentText(formatTimeDifference(taskRun.requiredTime, taskRun.fullTime))
                    .setSmallIcon(R.drawable.icon_monitor_circle)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setOngoing(true)
                    .setGroup(null)
                    .setVibrate(null)
                    .setSilent(true)

                val newNotification = notificationBuilder.build()
                notifications[idTask] = newNotification
                notificationManager.notify(idTask, newNotification)
            }
    }
    

    companion object {
        private const val CHANNEL_ID = "TasksServiceChannel"
        private const val NAME_CHANNEL = "TasksService"
    }
}