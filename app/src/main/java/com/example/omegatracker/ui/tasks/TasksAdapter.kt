package com.example.omegatracker.ui.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omegatracker.R
import com.example.omegatracker.entity.State
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.utils.formatTimeDifference


class TasksAdapter(
    private var tasksRun: List<TaskRun>,
    private val listener: TasksAdapterListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return if (tasksRun.isNotEmpty()) {
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
                DaysViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RunningTaskViewHolder -> {
                bindRunningTaskData(holder, position)
                holder.roadToTimer.setOnClickListener {
                    clickToTimer(position)
                }
            }
            is AllTaskViewHolder -> {
                val adjustedPosition = position - 1
                bindAllTaskData(holder, adjustedPosition)
                setTaskState(holder, adjustedPosition)
                holder.playTask.setOnClickListener {
                    setPlayButtonListener(adjustedPosition, holder)
                }
            }
            is DaysViewHolder -> {
                bindDaysViewData(holder, position)
            }
        }
    }

    class AllTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTask: TextView = itemView.findViewById(R.id.name_task)
        val nameProjectTask: TextView = itemView.findViewById(R.id.name_project)
        val stateTask: TextView = itemView.findViewById(R.id.task_tag)
        val playTask: ImageButton = itemView.findViewById(R.id.play_task)
        val timeTask: TextView = itemView.findViewById(R.id.time_task)
    }

    class RunningTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameRunningTask: TextView = itemView.findViewById(R.id.name_runningTask)
        val timeRunningTask: TextView = itemView.findViewById(R.id.time_runningTask)
        val roadToTimer: ImageButton = itemView.findViewById(R.id.to_timer)
    }

    class DaysViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val today: TextView = itemView.findViewById(R.id.today)
        val lookAll: TextView = itemView.findViewById(R.id.look_all)
    }

    private fun bindAllTaskData(holder: AllTaskViewHolder, position: Int) {
        holder.nameTask.text = tasksRun[position].name
        holder.nameProjectTask.text = tasksRun[position].projectName
        holder.stateTask.text = tasksRun[position].state
        holder.timeTask.text = formatTimeDifference(tasksRun[position].requiredTime, tasksRun[position].workedTime)
    }

    private fun bindDaysViewData(holder: DaysViewHolder, position: Int) {
        holder.today.text = "Сегодня"
        holder.lookAll.text = "Смотреть все"
    }

    private fun bindRunningTaskData(holder: RunningTaskViewHolder, position: Int) {
        holder.nameRunningTask.text = tasksRun[position].name
        val time = formatTimeDifference(tasksRun[position].requiredTime, tasksRun[position].fullTime)
        holder.timeRunningTask.text = time
    }

    fun updateTasksTime(taskRun: TaskRun) {
        tasksRun = listener.updateTimeTasks(tasksRun, taskRun)
        notifyDataSetChanged()
    }

    private fun setPlayButtonListener(position: Int, holder: AllTaskViewHolder) {
        tasksRun = listener.updateListTasks(tasksRun, position)
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

    companion object {
        private const val RUNNING_TASK_VIEW_TYPE = 0
        private const val DAYS_VIEW_TYPE = 1
        private const val ALL_TASK_VIEW_TYPE = 2
    }

}