package com.example.omegatracker.ui.timer

import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.databinding.ActivityTimerBinding
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.ui.base.BaseActivity
import com.example.omegatracker.utils.formatTimeDifference
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.seosh817.circularseekbar.CircularSeekBar
import kotlinx.coroutines.launch


class TimerActivity: BaseActivity(), TimerView {

    private val repositoryImpl = OmegaTrackerApplication.appComponent.repository()

    override val presenter: TimerPresenter by providePresenter {
        TimerPresenter(repositoryImpl)
    }

    private lateinit var binding: ActivityTimerBinding
    private lateinit var backButton: ImageButton
    private lateinit var nameTask: TextView
    private lateinit var stateTask: TextView
    private lateinit var descriptionTask: TextView
    private lateinit var time: TextView
    private lateinit var taskRun: TaskRun
    private lateinit var pause: ImageButton
    private lateinit var circularSeekBar: CircularSeekBar

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialization()
        interaction()
    }

    override fun initialization() {
        circularSeekBar = binding.circularSeekBar
        backButton = binding.back
        nameTask = binding.nameTask
        stateTask = binding.state
        descriptionTask = binding.description
        time = binding.time
        pause = binding.pause
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun interaction() {
        checkUpdateTask()
        startTimer()
        backToTasks()
        pauseTimer()
    }

    override fun backToTasks() {
        backButton.setOnClickListener {
            presenter.backAction()
        }
    }

    override fun checkUpdateTask() {
        taskRun = intent.getSerializableExtra("task") as TaskRun
        presenter.checkUpdateProperties(taskRun)
    }

    override fun startTimer() {
        presenter.updateTimeForTimer(taskRun)
        println(taskRun)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun pauseTimer() {
        pause.setOnClickListener {
            if (taskRun.isRunning == true) {
                taskRun.isRunning = false
                presenter.pauseTimer(taskRun)
            } else {
                taskRun.isRunning = true
                presenter.resumeTimer(taskRun)
            }
        }
    }

    override fun setAnimation(newProgress: Float, maxProgress: Float) {
        circularSeekBar.max = maxProgress
        circularSeekBar.progress = newProgress
    }


    override fun setView(taskRun: TaskRun) {
        nameTask.text = taskRun.name
        stateTask.text = taskRun.state
        descriptionTask.text = taskRun.description
    }

    override fun setTimer(taskRun: TaskRun) {
        lifecycleScope.launch {
            presenter.getTimeForTimer(taskRun).collect {
                time.text = formatTimeDifference(taskRun.requiredTime, it.fullTime)
            }
        }
    }

}