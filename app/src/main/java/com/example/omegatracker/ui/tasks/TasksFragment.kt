package com.example.omegatracker.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.R
import com.example.omegatracker.databinding.TasksFragmentBinding
import com.example.omegatracker.entity.task.TaskRun
import com.example.omegatracker.service.TasksService
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.activity.BaseActivity.Companion.startScreen
import com.example.omegatracker.ui.base.fragment.BaseFragment

class TasksFragment : BaseFragment(), TasksAdapterListener, TasksFragmentView, TasksCallback {

    private lateinit var tasksList: RecyclerView
    private lateinit var tasksListAdapter: TasksFragmentAdapter
    private lateinit var binding: TasksFragmentBinding

    override val presenter: TasksFragmentPresenter by providePresenter {
        TasksFragmentPresenter(OmegaTrackerApplication.appComponent.repository())
    }

    override val layoutRes: Int = R.layout.tasks_fragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layoutRes, container, false)
        binding = TasksFragmentBinding.bind(view)
        tasksList = binding.tasksList
        presenter.getTasksFromData()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showUserSettings()
    }

    override fun onStart() {
        super.onStart()
        presenter.checkTasksUpdates()
    }

    override fun setTasks(taskRuns: List<TaskRun>) {
        tasksList.adapter = TasksFragmentAdapter(taskRuns, this)
        tasksListAdapter = tasksList.adapter as TasksFragmentAdapter
    }


    override fun setNewTasksTime(taskRun: TaskRun) {
        tasksListAdapter.updateTasksTime(taskRun)
    }

    override fun exitProfile() {
        presenter.intentToAuth()
    }

    override fun updateListTasks(list: List<TaskRun>): List<TaskRun> {
        return presenter.updateListTasks(list)
    }

    override fun updateTimeTasks(list: List<TaskRun>, taskRun: TaskRun): List<TaskRun> {
        return presenter.updateTimeTasks(list, taskRun)
    }

    override fun clickToTimer(taskRun: TaskRun, list: List<TaskRun>) {
        presenter.intentToTimer(list, taskRun)
    }

    override fun startTask(taskRun: TaskRun) {
        presenter.startTask(taskRun)
    }

    override fun filterTasksByDate(filter: TaskFilter, tasksRun: List<TaskRun>): List<TaskRun> {
        return presenter.filterTasksByDate(filter, tasksRun)
    }


    override fun navigateScreen(screens: Screens) {
        activity?.let { startScreen(it, screens) }
    }

    override fun setServiceController(controller: TasksService.Controller) {
        println("CONTROLLER POLUCHEN " + controller)
        presenter.setController(controller)
    }

    private fun showUserSettings() {
        binding.iconProfile.setOnClickListener {
            val popUp = PopupMenu(requireContext(), it)
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
}