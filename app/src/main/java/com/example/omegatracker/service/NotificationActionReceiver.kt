package com.example.omegatracker.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.entity.NotificationActions
import com.example.omegatracker.entity.task.TaskRun

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action?.let { NotificationActions.valueOf(it) }
        val tasksManager = OmegaTrackerApplication.appComponent.tasksManager()
        Log.d("NotificationActionReceiver", "Action received: $action")

        when (action) {
            NotificationActions.PLAY_ALL -> {
                Log.d("NotificationActionReceiver", "PLAY_ALL action triggered")
            }

            NotificationActions.PAUSE_ALL -> {
                Log.d("NotificationActionReceiver", "PAUSE_ALL action triggered")
                tasksManager.pauseAllTasksRunners()
            }

            NotificationActions.PAUSE_TASK -> {
                val taskRun = intent.getParcelableExtra<TaskRun>("taskRun")
                Log.d(
                    "NotificationActionReceiver",
                    "PAUSE_TASK action triggered for task: $taskRun"
                )
                taskRun?.let { tasksManager.pauseTaskRunner(it) }
            }

            NotificationActions.PLAY_TASK -> {
                val taskRun = intent.getParcelableExtra<TaskRun>("taskRun")
                Log.d("NotificationActionReceiver", "PLAY_TASK action triggered for task: $taskRun")
                taskRun?.let { tasksManager.launchTaskRunner(taskRun) }
            }

            null -> Log.d("NotificationActionReceiver", "Received null action")
        }
    }
}
//TODO создать общий флоу