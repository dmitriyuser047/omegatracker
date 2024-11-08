package com.example.omegatracker.ui.main

import android.app.Fragment
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.R
import com.example.omegatracker.databinding.ActivityMainBinding
import com.example.omegatracker.di.AppComponent
import com.example.omegatracker.entity.Fragments
import com.example.omegatracker.entity.task.TaskRun
import com.example.omegatracker.service.TasksService
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.activity.BaseActivity
import com.example.omegatracker.ui.history.HistoryFragment
import com.example.omegatracker.ui.tasks.TasksFragment

class MainActivity() : BaseActivity(), MainView, AddCustomTaskListener {

    private lateinit var appComponent: AppComponent
    private lateinit var binding: ActivityMainBinding
    private val component = OmegaTrackerApplication.appComponent
    private var controller: TasksService.Controller? = null

    override val presenter: MainPresenter by providePresenter {
        MainPresenter(component.repository())
    }

    private val container = R.id.fragment_container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent = OmegaTrackerApplication.appComponent
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = Intent(this, TasksService::class.java)
        bindService(intent, serviceConnection, Context.BIND_IMPORTANT)
        startService(intent)
        initialization()
    }

    override fun initialization() {
        startFragment(Fragments.ALL_TASKS_FRAGMENT, false)
        binding.addTask.setOnClickListener {
            addTaskButton()
        }
        binding.historyButton.setOnClickListener {
            startFragment(Fragments.HISTORY_FRAGMENT, true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
        this.finish()
    }

    override fun addTaskButton() {
        val addTasksDialog = OmegaTrackerApplication.appComponent.addTaskDialog()
        addTasksDialog.show(supportFragmentManager, "addTasksDialog")
    }

    override fun startFragment(fragment: Fragments, addBackStack: Boolean) {
        val fragmentInstance = when (fragment) {
                Fragments.ALL_TASKS_FRAGMENT -> TasksFragment()
                Fragments.HISTORY_FRAGMENT -> HistoryFragment()
                Fragments.STATISTICS_FRAGMENT -> TODO()
            }
        val transaction = supportFragmentManager.beginTransaction()
            .replace(container, fragmentInstance, fragment.javaClass.simpleName)
        if (addBackStack) {
            transaction.addToBackStack(fragment.javaClass.simpleName)
        }
        transaction.commit()
    }

    override fun navigateScreen(screens: Screens) {
        startScreen(this, screens)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            controller = service as TasksService.Controller
            controller?.let { getController(it) }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            controller = null
        }
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
        controller?.let { getController(it) }
    }

    private fun getController(controller: TasksService.Controller) {
        controller.let {
            val tasksFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? TasksFragment
            tasksFragment?.setServiceController(it)
        }
    }

    override fun onDialogDismiss() {
    }

    override fun onTaskAdded(task: TaskRun) {
        presenter.addNewTask(task)
    }
}
