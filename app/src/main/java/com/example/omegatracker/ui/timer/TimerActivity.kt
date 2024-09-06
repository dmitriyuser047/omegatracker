package com.example.omegatracker.ui.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.R
import com.example.omegatracker.databinding.ActivityTimerBinding
import com.example.omegatracker.entity.TimerButtons
import com.example.omegatracker.entity.task.State
import com.example.omegatracker.entity.task.TaskRun
import com.example.omegatracker.service.TasksService
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.BaseActivity
import com.example.omegatracker.utils.formatTimeDifference
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
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
        buttonActions()
    }

    override fun buttonActions() {
        binding.startTask.setOnClickListener {
            presenter.resumeTimer(taskRunner)
            updateButtonVisibility(TimerButtons.START)
        }
        binding.pauseButton.setOnClickListener {
            if (taskRunner.isRunning == false) {
                presenter.resumeTimer(taskRunner)
                updateButtonVisibility(TimerButtons.START)
            } else {
                presenter.pauseTimer(taskRunner)
                updateButtonVisibility(TimerButtons.PAUSE)
            }
        }
        binding.completeButton.setOnClickListener {
            presenter.completeTask(taskRunner)
            updateButtonVisibility(TimerButtons.COMPLETE)
        }
    }

    override fun checkUpdateTask() {
        val receivedTaskRun = intent.getParcelableExtra<TaskRun>("taskRun")
        val taskId = receivedTaskRun?.id
        if (taskId != null) {
            presenter.findTaskRun(taskId)
        }
    }

    override fun setAnimation(newProgress: Float, maxProgress: Float) {
        progressBar.setMaxProgress(maxProgress)
        progressBar.setProgress(newProgress)
    }

    override fun changeState(taskRun: TaskRun, state: State) {
        when (state) {
            State.InProgress -> taskRun.state = getString(State.InProgress.localState)
            State.Open -> taskRun.state = getString(State.Open.localState)
            State.InPause -> taskRun.state = getString(State.InPause.localState)
            State.Stopped -> taskRun.state = getString(State.Stopped.localState)
        }
    }

    override fun navigateTo(screens: Screens) {
        createIntent(this, screens)
    }


    override fun setView(taskRun: TaskRun) {
        taskRunner = taskRun
        binding.nameTask.text = taskRun.name
        println(taskRun.description)
        binding.bottomSheetDescription.description.text = taskRun.description
            ?: getString(R.string.empty_description)
        if (taskRun.isRunning == true) {
            binding.state.text = getString(R.string.in_progress)
        } else binding.state.text = getString(R.string.open)

        presenter.updateTimeForTimer(taskRun)
    }



    override fun setTimer(taskRun: TaskRun) {
        lifecycleScope.launch {
            presenter.getTimeForTimer(taskRun)?.collect {
                binding.time.text = formatTimeDifference(taskRun.requiredTime, it.fullTime)
            }
        }
    }
    override fun backToTasks() {
        binding.backButton.setOnClickListener {
            presenter.backAction()
        }
    }


    override fun updateButtonVisibility(currentState: TimerButtons) {
        when (currentState) {
            TimerButtons.START -> {
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

            TimerButtons.PAUSE -> {
                binding.startTask.isVisible = false
                binding.textStart.isVisible = false

                binding.pauseButton.setImageResource(R.drawable.play_for_timer)
                binding.textPause.text = getString(R.string.click_to_resume)

                binding.completeButton.isVisible = true
                binding.textComplete.isVisible = true

                binding.state.text = getString(R.string.pause)
            }

            TimerButtons.COMPLETE -> {
                binding.startTask.isVisible = true
                binding.textStart.isVisible = true

                binding.pauseButton.isVisible = false
                binding.textPause.isVisible = false

                binding.completeButton.isVisible = false
                binding.textComplete.isVisible = false

                binding.state.text = getString(R.string.stopped)
            }

            TimerButtons.HIDE_ALL ->  {
                binding.startTask.isVisible = false
                binding.textStart.isVisible = false

                binding.pauseButton.isVisible = false
                binding.textPause.isVisible = false

                binding.completeButton.isVisible = false
                binding.textComplete.isVisible = false
            }
        }
    }

}