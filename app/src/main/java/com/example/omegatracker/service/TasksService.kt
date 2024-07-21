package com.example.omegatracker.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.entity.TaskRun
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton


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
            showNotification(taskRun)
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

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        notificationChannel = NotificationChannel(CHANNEL_ID, NAME_CHANNEL, importance)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(task: TaskRun) {
//        val notificationId = task.id.hashCode()
//        val notificationContentText = formatTimeDifference(task.requiredTime, task.fullTime)
//
//        val notification: Notification = NotificationCompat.Builder(this, notificationChannel.id)
//            .setContentTitle(task.name)
//            .setContentText(notificationContentText)
//            .setSmallIcon(R.drawable.icon_monitor_circle)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setSound(null)
//            .setVibrate(null)
//            .setOnlyAlertOnce(true)
//            .build()
//
//        notificationManager.notify(notificationId, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun subscribeToTaskUpdates(taskRun: TaskRun) {
//        CoroutineScope(Dispatchers.IO).launch {
//            tasksManager.getTaskUpdates(taskRun.id).collect {
//                showNotification(it)
//            }
//        }
    }

    companion object {
        private const val CHANNEL_ID = "TasksServiceChannel"
        private const val NAME_CHANNEL = "TasksService"
    }

}