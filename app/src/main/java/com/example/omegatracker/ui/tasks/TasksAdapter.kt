package com.example.omegatracker.ui.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omegatracker.R
import com.example.omegatracker.entity.task.State
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.utils.formatTimeDifference

enum class TaskFilter {
    Today,
    AllTasks
}


class TasksAdapter(
    private var tasksRun: List<TaskRun>,
    private val listener: TasksAdapterListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var valueFilter = TaskFilter.AllTasks

    private var todayTasksRun: List<TaskRun> = listOf()

    override fun getItemCount(): Int {
        return if (todayTasksRun.isNotEmpty()) {
            todayTasksRun.size + 1
        } else if (tasksRun.isNotEmpty()){
            tasksRun.size + 1
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        val runningTaskCount = tasksRun.count { it.isRunning == true }
        return when {
            position < runningTaskCount -> RUNNING_TASK_VIEW_TYPE
            position == runningTaskCount -> DAYS_VIEW_TYPE
            else -> ALL_TASK_VIEW_TYPE
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            RUNNING_TASK_VIEW_TYPE -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.running_task_recycler, parent, false)
                RunningTaskViewHolder(itemView)
            }
            ALL_TASK_VIEW_TYPE -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tasks_recycler, parent, false)
                AllTaskViewHolder(itemView)
            }
            DAYS_VIEW_TYPE -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.days_view_type, parent, false)
                FilterViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RunningTaskViewHolder -> {
                holder.update(tasksRun[position])
                holder.roadToTimer.setOnClickListener {
                    clickToTimer(position)
                }
            }
            is FilterViewHolder -> {
                when (valueFilter) {
                    TaskFilter.AllTasks -> {
                        valueFilter = TaskFilter.Today
                        holder.lookAll.text = holder.itemView.context.getString(R.string.view_today_tasks)
                        holder.today.text = holder.itemView.context.getString(R.string.all_tasks)
                    }
                    TaskFilter.Today -> {
                        valueFilter = TaskFilter.AllTasks
                        holder.lookAll.text = holder.itemView.context.getString(R.string.look_all)
                        holder.today.text = holder.itemView.context.getString(R.string.today)
                    }
                }
                holder.lookAll.setOnClickListener {
                    filterDate(valueFilter)
                }
            }
            is AllTaskViewHolder -> {
                val offsetPosition = position - 1
                holder.update(tasksRun[offsetPosition])
                holder.playTask.setOnClickListener {
                    setPlayButtonListener(offsetPosition, holder)
                }
                setTaskState(holder, offsetPosition)
                holder.taskArea.setOnClickListener {
                    clickToTimer(offsetPosition)
                }
            }
        }
    }

    class AllTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTask: TextView = itemView.findViewById(R.id.name_task)
        val nameProjectTask: TextView = itemView.findViewById(R.id.name_project)
        val stateTask: TextView = itemView.findViewById(R.id.task_tag)
        val playTask: ImageButton = itemView.findViewById(R.id.play_task)
        val timeTask: TextView = itemView.findViewById(R.id.time_task)
        val taskArea: View = itemView.findViewById(R.id.task)
        fun update(task: TaskRun) {
            nameTask.text = task.name
            nameProjectTask.text = task.projectName
            stateTask.text = task.state
            timeTask.text = formatTimeDifference(task.requiredTime, task.workedTime)
        }
    }

    class RunningTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameRunningTask: TextView = itemView.findViewById(R.id.name_runningTask)
        val timeRunningTask: TextView = itemView.findViewById(R.id.time_runningTask)
        val roadToTimer: ImageButton = itemView.findViewById(R.id.to_timer)
        fun update(task: TaskRun) {
            nameRunningTask.text = task.name
            timeRunningTask.text = formatTimeDifference(task.requiredTime, task.fullTime)
        }
    }

    class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val today: TextView = itemView.findViewById(R.id.today)
        val lookAll: Button = itemView.findViewById(R.id.look_all)
    }
    fun updateTasksTime(taskRun: TaskRun) {
        tasksRun = listener.updateTimeTasks(tasksRun, taskRun)
        val position = tasksRun.indexOfFirst { it.id == taskRun.id }
        notifyItemChanged(position)
    }

    private fun setPlayButtonListener(position: Int, holder: AllTaskViewHolder) {
        listener.startTask(tasksRun[position])
        tasksRun = listener.updateListTasks(tasksRun)
        tasksRun[position].state = holder.itemView.context.getString(State.InProgress.localState)
        notifyDataSetChanged()
    }

    private fun clickToTimer(position: Int) {
        listener.clickToTimer(tasksRun[position], tasksRun)
    }

    private fun setTaskState(holder: AllTaskViewHolder, position: Int) {
        val matchingState = State.entries.find { "In Progress" == tasksRun[position].state }

        if (matchingState != null) {
            holder.stateTask.text =  holder.itemView.context.getString(matchingState.localState)
        } else {
            holder.stateTask.text =  holder.itemView.context.getString(State.Open.localState)
        }
    }

    private fun filterDate(currentFilter: TaskFilter) {
        todayTasksRun = listener.filterTasksByDate(currentFilter, tasksRun)
        notifyDataSetChanged()
    }

    companion object {
        private const val RUNNING_TASK_VIEW_TYPE = 0
        private const val DAYS_VIEW_TYPE = 1
        private const val ALL_TASK_VIEW_TYPE = 2
    }

}