package com.example.omegatracker.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.R
import com.example.omegatracker.entity.NotificationActions
import com.example.omegatracker.entity.task.TaskRun
import com.example.omegatracker.ui.main.MainActivity
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
    private val tasksManager = OmegaTrackerApplication.appComponent.tasksManager()
    private val notificationBuilders = mutableMapOf<Int, NotificationCompat.Builder>()

    inner class Controller : Binder(), IController {

        override fun startTask(taskRun: TaskRun) {
            tasksManager.launchTaskRunner(taskRun)
            //createNotification(taskRun)
        }

        override fun getUpdatedTask(taskId: String): Flow<TaskRun> {
            taskUpdates(tasksManager.getTaskUpdates(taskId))
            return tasksManager.getTaskUpdates(taskId)
        }

        override fun pauseTask(taskRun: TaskRun) {
            tasksManager.pauseTaskRunner(taskRun)
        }
    }

    override fun onBind(intent: Intent?): IBinder = Controller()

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }


    private fun createActionIntent(action: NotificationActions, taskRun: TaskRun?): PendingIntent {
        val intent = Intent(this, NotificationActionReceiver::class.java).apply {
            this.action = action.toString()
            putExtra("taskRun", taskRun)
        }
        return PendingIntent.getBroadcast(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            NAME_CHANNEL,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableVibration(true)
            setSound(null, null)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createHeadNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.icon_monitor_circle)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setGroup(GROUP_ID)
            setContentIntent(createHeadNotificationIntent())
            setGroupSummary(true)
        }
        notificationManager.notify(0, builder.build())
    }

    private fun createControlNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.icon_monitor_circle)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setGroup(GROUP_ID)
            setContent(createNotificationControlView())
        }
        notificationManager.notify(1, builder.build())
    }

    private fun createNotification(taskRun: TaskRun) {
        createHeadNotification()
        createControlNotification()
        val notificationId = taskRun.id.hashCode()
        val notificationIntent = createNotificationIntent(taskRun)

        val notificationView = createNotificationView(taskRun)
        val notificationViewExtended = createExtendedNotificationView(taskRun)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setStyle(NotificationCompat.DecoratedCustomViewStyle())
            setContent(notificationView)
            setCustomBigContentView(notificationViewExtended)
            setPriority(NotificationCompat.PRIORITY_LOW)
            setOngoing(true)
            setContentIntent(notificationIntent)
            setSmallIcon(R.drawable.icon_monitor_circle)
            setGroup(GROUP_ID)
        }

        notificationBuilders[notificationId] = notificationBuilder
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun createNotificationView(taskRun: TaskRun): RemoteViews {
        return RemoteViews(packageName, R.layout.notifications_layout).apply {
            setTextViewText(R.id.notification_name_task, taskRun.name)
            setTextViewText(
                R.id.notification_time,
                formatTimeDifference(taskRun.requiredTime, taskRun.fullTime)
            )
        }
    }

    private fun createExtendedNotificationView(taskRun: TaskRun): RemoteViews {
        return RemoteViews(packageName, R.layout.notifications_extended_layout).apply {
            setTextViewText(R.id.notification_task_name_extended, taskRun.name)
            setTextViewText(
                R.id.notification_time,
                formatTimeDifference(taskRun.requiredTime, taskRun.fullTime)
            )
            setOnClickPendingIntent(
                R.id.notification_play,
                createActionIntent(NotificationActions.PLAY_TASK, taskRun)
            )
            setOnClickPendingIntent(
                R.id.notification_pause,
                createActionIntent(NotificationActions.PAUSE_TASK, taskRun)
            )
        }
    }

    private fun createNotificationControlView(): RemoteViews {
        return RemoteViews(packageName, R.layout.notification_control_layout).apply {
            setOnClickPendingIntent(
                R.id.play_all,
                createActionIntent(NotificationActions.PLAY_ALL, null)
            )
            setOnClickPendingIntent(
                R.id.pause_all,
                createActionIntent(NotificationActions.PAUSE_ALL, null)
            )
        }
    }

    private fun createHeadNotificationIntent(): PendingIntent {
        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotificationIntent(taskRun: TaskRun): PendingIntent {
        return PendingIntent.getActivity(
            this,
            taskRun.id.hashCode(),
            Intent(this, TimerActivity::class.java).apply {
                putExtra("taskRun", taskRun)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun taskUpdates(flow: Flow<TaskRun>) {
        CoroutineScope(Dispatchers.IO).launch {
            flow.collect { updateNotifications(it) }
        }
    }

    private fun updateNotifications(taskRun: TaskRun) {
        val notificationId = taskRun.id.hashCode()
        notificationBuilders[notificationId]?.let { notificationBuilder ->
            val notificationView = createNotificationView(taskRun)
            val notificationViewExtended = createExtendedNotificationView(taskRun)

            notificationBuilder
                .setContent(notificationView)
                .setCustomBigContentView(notificationViewExtended)

            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }

    companion object {
        private const val CHANNEL_ID = "TasksSe"
        private const val NAME_CHANNEL = "TasksService"
        private const val GROUP_ID = "TasksGroup"
    }
}