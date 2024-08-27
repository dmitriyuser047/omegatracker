package com.example.omegatracker.ui.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.R
import com.example.omegatracker.databinding.ActivityTimerBinding
import com.example.omegatracker.entity.ClicksButton
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
    private lateinit var taskRunner: TaskRun
    private lateinit var progressBar: TimerProgressBar


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
    }


    override fun interaction() {
        checkUpdateTask()
        backToTasks()
        binding.startTask.setOnClickListener {
            presenter.resumeTimer(taskRunner)
            updateButtonVisibility(ClicksButton.START)
        }
        binding.pauseButton.setOnClickListener {
            if (taskRunner.isRunning == false) {
                presenter.resumeTimer(taskRunner)
                updateButtonVisibility(ClicksButton.START)
            } else {
                presenter.pauseTimer(taskRunner)
                updateButtonVisibility(ClicksButton.PAUSE)
            }
        }
        binding.completeButton.setOnClickListener {
            presenter.completeTask(taskRunner)
            updateButtonVisibility(ClicksButton.COMPLETE)
        }
    }

    override fun checkUpdateTask() {
        val navigationData = intent.getParcelableExtra<NavigationData>("navigation_data")
        val taskId = navigationData?.info
        if (taskId != null) {
            presenter.findTaskRun(taskId)
        }
    }

    override fun setAnimation(newProgress: Float, maxProgress: Float) {
        progressBar.setMaxProgress(maxProgress)
        progressBar.setProgress(newProgress)
    }


    override fun setView(taskRun: TaskRun) {
        taskRunner = taskRun
        binding.nameTask.text = taskRun.name
        if (taskRun.isRunning == true) {
            binding.state.text = getString(R.string.in_progress)
        } else binding.state.text = getString(R.string.open)
        presenter.updateTimeForTimer(taskRun)
    }



    override fun setTimer(taskRun: TaskRun) {
        lifecycleScope.launch {
            presenter.getTimeForTimer(taskRun)?.collect {
                binding.time.text = formatTimeDifference(taskRun.requiredTime, it.fullTime)
                println("Zadacha ${taskRun.name} - ${formatTimeDifference(taskRun.requiredTime, it.fullTime)}")
            }
        }
    }
    override fun backToTasks() {
        binding.backButton.setOnClickListener {
            presenter.backAction()
        }
    }


    override fun updateButtonVisibility(currentState: ClicksButton) {
        when (currentState) {
            ClicksButton.START -> {
                binding.startTask.isVisible = false
                binding.textStart.isVisible = false

                binding.pauseButton.isVisible = true
                binding.pauseButton.setImageResource(R.drawable.pause_timer)
                binding.textPause.text = getString(R.string.click_to_pause)
                binding.textPause.isVisible = true
                binding.completeButton.isVisible = true

                binding.textComplete.isVisible = true

                binding.state.text = getString(R.string.in_progress)
            }

            ClicksButton.PAUSE -> {
                binding.startTask.isVisible = false
                binding.textStart.isVisible = false

                binding.pauseButton.setImageResource(R.drawable.play)
                binding.textPause.text = getString(R.string.click_to_resume)

                binding.completeButton.isVisible = true
                binding.textComplete.isVisible = true

                binding.state.text = getString(R.string.pause)
            }

            ClicksButton.COMPLETE -> {
                binding.startTask.isVisible = true
                binding.textStart.isVisible = true

                binding.pauseButton.isVisible = false
                binding.textPause.isVisible = false

                binding.completeButton.isVisible = false
                binding.textComplete.isVisible = false

                binding.state.text = getString(R.string.stopped)
            }
        }
    }

}