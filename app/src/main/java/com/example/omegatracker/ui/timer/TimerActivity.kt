package com.example.omegatracker.ui.timer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.R
import com.example.omegatracker.databinding.ActivityTimerBinding
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.ui.base.BaseActivity
import com.example.omegatracker.utils.formatTimeDifference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit


class TimerActivity: BaseActivity(), TimerView {

    private val repositoryImpl = OmegaTrackerApplication.appComponent.repository()

    override val presenter: TimerPresenter by providePresenter {
        TimerPresenter(repositoryImpl)
    }

    private lateinit var binding: ActivityTimerBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var backButton: ImageButton
    private lateinit var nameTask: TextView
    private lateinit var stateTask: TextView
    private lateinit var descriptionTask: TextView
    private lateinit var time: TextView
    private lateinit var taskRun: TaskRun
    private lateinit var pause: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialization()
        interaction()
    }

    override fun initialization() {
        progressBar = binding.progressbar
        backButton = binding.back
        nameTask = binding.nameTask
        stateTask = binding.state
        descriptionTask = binding.description
        time = binding.time
        pause = binding.pause
    }

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

    override fun setAnimation(newProgress: Int, maxProgress: Int) {
        progressBar.max = maxProgress
        progressBar.progress = newProgress
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