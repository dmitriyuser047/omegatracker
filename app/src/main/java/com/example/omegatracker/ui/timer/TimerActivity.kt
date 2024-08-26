package com.example.omegatracker.ui.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.databinding.ActivityTimerBinding
import com.example.omegatracker.entity.NavigationData
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.service.TasksService
import com.example.omegatracker.ui.base.BaseActivity
import com.example.omegatracker.utils.formatTimeDifference
import kotlinx.coroutines.launch


class TimerActivity: BaseActivity(), TimerView {

    private val repositoryImpl = OmegaTrackerApplication.appComponent.repository()

    override val presenter: TimerPresenter by providePresenter {
        TimerPresenter(repositoryImpl)
    }

    private lateinit var binding: ActivityTimerBinding
    private lateinit var backButton: ImageButton
    private lateinit var nameTask: TextView
    private lateinit var time: TextView
    private lateinit var taskRunner: TaskRun
    private lateinit var pause: ImageButton
    private lateinit var progressBar: TimerProgressBar

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialization()
        interaction()
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TasksService.Controller
            presenter.setController(binder)
        }

        override fun onServiceDisconnected(className: ComponentName) {
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, TasksService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT)
        startService(intent)
    }

    override fun initialization() {
        progressBar = binding.customProgressBar
        backButton = binding.back
        nameTask = binding.nameTask
        time = binding.time
        pause = binding.pause
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun interaction() {
        checkUpdateTask()
        backToTasks()
        pauseTimer()
    }

    override fun backToTasks() {
        backButton.setOnClickListener {
            presenter.backAction()
        }
    }


    override fun checkUpdateTask() {
        val navigationData = intent.getParcelableExtra<NavigationData>("navigation_data")
        val taskId = navigationData?.info
        if (taskId != null) {
            presenter.findTaskRun(taskId)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun pauseTimer() {
        pause.setOnClickListener {
            if (taskRunner.isRunning == true) {
                presenter.pauseTimer(taskRunner)
            } else {
                presenter.resumeTimer(taskRunner)
            }
        }
    }

    override fun setAnimation(newProgress: Float, maxProgress: Float) {
        progressBar.setMaxProgress(maxProgress)
        progressBar.setProgress(newProgress)
    }


    override fun setView(taskRun: TaskRun) {
        taskRunner = taskRun
        nameTask.text = taskRun.name
        presenter.updateTimeForTimer(taskRun)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setTimer(taskRun: TaskRun) {
        lifecycleScope.launch {
            presenter.getTimeForTimer(taskRun)?.collect {
                time.text = formatTimeDifference(taskRun.requiredTime, it.fullTime)
                println("Zadacha ${taskRun.name} - ${formatTimeDifference(taskRun.requiredTime, it.fullTime)}")
            }
        }
    }

}