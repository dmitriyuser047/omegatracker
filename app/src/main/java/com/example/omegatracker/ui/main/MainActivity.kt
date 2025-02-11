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
import com.example.omegatracker.entity.ScreensButtons
import com.example.omegatracker.entity.task.TaskRun
import com.example.omegatracker.service.TasksService
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.activity.BaseActivity
import com.example.omegatracker.ui.history.HistoryFragment
import com.example.omegatracker.ui.statistics.StatisticsFragment
import com.example.omegatracker.ui.tasks.TasksFragment

class MainActivity : BaseActivity(), MainView, AddCustomTaskListener {

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
        startService()
        initialization()
        addOnBackStackChangedListener()
    }

    override fun initialization() {
        startFragment(Fragments.TASKS_FRAGMENT, false)
        binding.addTask.setOnClickListener {
            addTaskButton()
        }
        binding.historyButton.setOnClickListener {
            startFragment(Fragments.HISTORY_FRAGMENT, true)
        }
        binding.statisticsButton.setOnClickListener {
            startFragment(Fragments.STATISTICS_FRAGMENT, true)
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
                Fragments.TASKS_FRAGMENT -> {
                    updateStateBottomNavigation(ScreensButtons.MAIN_SCREEN)
                    TasksFragment()
                }
                Fragments.HISTORY_FRAGMENT -> {
                    updateStateBottomNavigation(ScreensButtons.HISTORY_SCREEN)
                    HistoryFragment()
                }
                Fragments.STATISTICS_FRAGMENT -> {
                    updateStateBottomNavigation(ScreensButtons.STATISTICS_SCREEN)
                    StatisticsFragment()
                }
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

    private fun startService() {
        val intent = Intent(this, TasksService::class.java)
        bindService(intent, serviceConnection, Context.BIND_IMPORTANT)
        startService(intent)
    }

    private fun addOnBackStackChangedListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            updateStateBottomNavigation(getCurrentScreen())
        }
    }

    private fun getCurrentScreen(): ScreensButtons {
        return when (supportFragmentManager.findFragmentById(container)) {
            is TasksFragment -> ScreensButtons.MAIN_SCREEN
            is HistoryFragment -> ScreensButtons.HISTORY_SCREEN
            is StatisticsFragment -> ScreensButtons.STATISTICS_SCREEN
            else -> ScreensButtons.MAIN_SCREEN
        }
    }

    private fun updateStateBottomNavigation(state: ScreensButtons) {
        when (state) {
            ScreensButtons.HISTORY_SCREEN -> {
                binding.historyButton.setImageResource(R.drawable.time_clicked)
                binding.statisticsButton.setImageResource(R.drawable.statics)
            }
            ScreensButtons.MAIN_SCREEN -> {
                binding.historyButton.setImageResource(R.drawable.time)
                binding.statisticsButton.setImageResource(R.drawable.statics)
                binding.addTask.setImageResource(R.drawable.add)
            }
            ScreensButtons.STATISTICS_SCREEN -> {
                binding.historyButton.setImageResource(R.drawable.time)
                binding.statisticsButton.setImageResource(R.drawable.statistics_clicked)
            }
        }
    }
}
