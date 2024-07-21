package com.example.omegatracker.ui.tasks

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.R
import com.example.omegatracker.databinding.ActivityTasksBinding
import com.example.omegatracker.di.AppComponent
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.service.TasksService
import com.example.omegatracker.ui.base.BaseActivity
import com.example.omegatracker.ui.timer.TimerActivity
import kotlinx.coroutines.launch
import kotlin.time.Duration


class TasksActivity : BaseActivity(), TasksView, TasksTrackingListener, TasksAdapterListener {

    private lateinit var appComponent: AppComponent
    private lateinit var binding: ActivityTasksBinding
    private lateinit var tasksList: RecyclerView
    private lateinit var tasksListAdapter: TasksAdapter
    private val component = OmegaTrackerApplication.appComponent

    override val presenter: TasksPresenter by providePresenter {
        TasksPresenter(OmegaTrackerApplication.appComponent.repository())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent = OmegaTrackerApplication.appComponent
        binding = ActivityTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tasksList = binding.tasksList
        showIconProfile()
        showUserSettings()
        startService()
    }

    override fun exitProfile() {
        presenter.intentToAuth()
    }


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TasksService.Controller
            presenter.onServiceConnected(binder)
            println("ServiceConnected")
        }

        override fun onServiceDisconnected(className: ComponentName) {
            presenter.onServiceDisconnected()
            println("ServiceDisconnected")
        }
    }

    override fun startService() {
        val intent = Intent(this, TasksService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun setNewTasksTime(taskRun: TaskRun) {
        lifecycleScope.launch {
            presenter.getUpdateTasksTime(taskRun).collect {
                tasksListAdapter.updateTasksTime(taskRun)
            }
        }
    }


    override fun intentToTimer(intent: Intent) {
        startActivity(intent)
    }

    override fun showTasks(taskRun: List<TaskRun>) {
        tasksList.adapter = TasksAdapter(taskRun, this)
        tasksListAdapter = tasksList.adapter as TasksAdapter
    }

    override fun showIconProfile() {
        val url = "${component.userManager().getUserUrl()}${component.userManager().getIcon()}"
        val uri = Uri.parse(url)
        try {
            Glide.with(this)
                .load(uri)
                .fitCenter()
                .error(R.drawable.ic_processing_icon1)
                .into(binding.iconProfile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun startTaskTime(taskRun: TaskRun) {
//        component.tasksTracking().setTask(taskRun)
        presenter.startTask(taskRun)
    }

    override fun showUserSettings() {
        binding.iconProfile.setOnClickListener {
            val popUp = PopupMenu(this, it)
            popUp.menuInflater.inflate(R.menu.popup_profile, popUp.menu)
            popUp.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu1 -> {
                        exitProfile()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
            popUp.show()
        }
    }

    override fun updateListTasks(list: List<TaskRun>, position: Int): List<TaskRun> {
       return presenter.updateListTasks(list, position)
    }

    override fun updateTimeTasks(list: List<TaskRun>, taskRun: TaskRun): List<TaskRun> {
        return presenter.updateTimeTasks(list, taskRun)
    }

    override fun clickToTimer(taskRun: TaskRun, tasksRuns: List<TaskRun>) {
        intent = Intent(this, TimerActivity::class.java)
        intent.putExtra("task", taskRun)
        presenter.intentToTimer(intent,tasksRuns)
    }

    override fun onDialogDismiss(timeLimit: Duration) {

    }
}