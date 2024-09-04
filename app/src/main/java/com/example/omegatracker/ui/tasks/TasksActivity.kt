package com.example.omegatracker.ui.tasks

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.ImageViewTarget
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.R
import com.example.omegatracker.databinding.ActivityTasksBinding
import com.example.omegatracker.di.AppComponent
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.service.TasksService
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.BaseActivity
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
        initialization()
    }

    override fun initialization() {
        tasksList = binding.tasksList
        showIconProfile()
        showUserSettings()
        val intent = Intent(this, TasksService::class.java)
        bindService(intent, serviceConnection, Context.BIND_IMPORTANT)
        startService(intent)
    }

    override fun navigateTo(screens: Screens) {
        createIntent(this, screens)
    }

    override fun exitProfile() {
        presenter.intentToAuth()
    }


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TasksService.Controller
            presenter.setController(binder)
        }

        override fun onServiceDisconnected(className: ComponentName) {
        }
    }

    override fun setNewTasksTime(taskRun: TaskRun) {
        tasksListAdapter.updateTasksTime(taskRun)
        println("time in activity - " + taskRun.fullTime)
    }

    override fun showTasks(taskRun: List<TaskRun>) {
        tasksList.adapter = TasksAdapter(taskRun, this)
        tasksListAdapter = tasksList.adapter as TasksAdapter
    }

    override fun showIconProfile() {
        val url = "${component.userManager().getUserUrl()}${component.userManager().getIcon()}"
        Glide.with(this)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(object : ImageViewTarget<Drawable>(binding.iconProfile) {
                override fun setResource(resource: Drawable?) {
                    binding.iconProfile.setImageDrawable(resource)
                }
            })
    }

    override fun startTaskTime(taskRun: TaskRun) {
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

    override fun updateListTasks(list: List<TaskRun>): List<TaskRun> {
       return presenter.updateListTasks(list)
    }

    override fun updateTimeTasks(list: List<TaskRun>, taskRun: TaskRun): List<TaskRun> {
        return presenter.updateTimeTasks(list, taskRun)
    }

    override fun clickToTimer(taskRun: TaskRun, tasksRuns: List<TaskRun>) {
        presenter.intentToTimer(tasksRuns, taskRun)
    }

    override fun startTask(taskRun: TaskRun) {
        presenter.startTask(taskRun)
    }

    override fun filterTasksByDate(filter: TaskFilter, tasksRun: List<TaskRun>): List<TaskRun> {
       return presenter.filterTasksByDate(filter, tasksRun)
    }

    override fun onDialogDismiss(timeLimit: Duration) {

    }
}
