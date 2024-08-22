package com.example.omegatracker.ui.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omegatracker.R
import com.example.omegatracker.entity.State
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.utils.formatTimeDifference

enum class TaskFilterAdapter {
    Today,
    AllTasks
}

class TasksAdapter(
    tasksRun: List<TaskRun>,
    private val listener: TasksAdapterListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listWorker = ListWorker(tasksRun)
    inner class ListWorker(private var allTasksRuns: List<TaskRun>) {
        var currentFilter: TaskFilterAdapter = TaskFilterAdapter.AllTasks
        private var filteredTaskRuns: List<TaskRun> = allTasksRuns
        val data: List<TaskRun>
            get() = filteredTaskRuns
        fun updateData(newData: List<TaskRun>) {
            when (currentFilter) {
                TaskFilterAdapter.Today -> filteredTaskRuns = newData
                TaskFilterAdapter.AllTasks -> allTasksRuns = newData
            }
            applyFilter()
        }
        fun setFilter(newFilter: TaskFilterAdapter) {
            currentFilter = newFilter
            applyFilter()
        }
        private fun applyFilter() {
            filteredTaskRuns = when (currentFilter) {
                TaskFilterAdapter.Today -> listener.filterTasksByDate(
                    TaskFilterAdapter.Today,
                    allTasksRuns
                )
                TaskFilterAdapter.AllTasks -> allTasksRuns
            }
        }
    }

    override fun getItemCount(): Int {
        return if (listWorker.data.isNotEmpty()) listWorker.data.size + 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        val runningTaskCount = listWorker.data.count { it.isRunning == true }
        return when {
            position < runningTaskCount -> RUNNING_TASK_VIEW_TYPE
            position == runningTaskCount -> DAYS_VIEW_TYPE
            else -> ALL_TASK_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            RUNNING_TASK_VIEW_TYPE -> {
                val view = inflater.inflate(R.layout.running_task_recycler, parent, false)
                RunningTaskViewHolder(view)
            }

            DAYS_VIEW_TYPE -> {
                val view = inflater.inflate(R.layout.days_view_type, parent, false)
                DaysViewHolder(view)
            }

            ALL_TASK_VIEW_TYPE -> {
                val view = inflater.inflate(R.layout.tasks_recycler, parent, false)
                AllTaskViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RunningTaskViewHolder -> holder.bind(listWorker.data[position]) { task ->
                clickToTimer(position)
            }

            is DaysViewHolder -> holder.bind {
                filterDate(holder)
            }

            is AllTaskViewHolder -> holder.bind(listWorker.data[position - 1]) { task ->
                setPlayButtonListener(position - 1, holder)
            }
        }
    }

    class AllTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTask: TextView = itemView.findViewById(R.id.name_task)
        private val nameProjectTask: TextView = itemView.findViewById(R.id.name_project)
        private val stateTask: TextView = itemView.findViewById(R.id.task_tag)
        private val playTask: ImageButton = itemView.findViewById(R.id.play_task)
        private val timeTask: TextView = itemView.findViewById(R.id.time_task)

        fun bind(task: TaskRun, onPlayClickListener: (TaskRun) -> Unit) {
            nameTask.text = task.name
            nameProjectTask.text = task.projectName
            stateTask.text = task.state
            timeTask.text = formatTimeDifference(task.requiredTime, task.workedTime)
            playTask.setOnClickListener { onPlayClickListener(task) }
        }
    }

    class RunningTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameRunningTask: TextView = itemView.findViewById(R.id.name_runningTask)
        private val timeRunningTask: TextView = itemView.findViewById(R.id.time_runningTask)
        private val roadToTimer: ImageButton = itemView.findViewById(R.id.to_timer)

        fun bind(task: TaskRun, onTimerClickListener: (TaskRun) -> Unit) {
            nameRunningTask.text = task.name
            timeRunningTask.text = formatTimeDifference(task.requiredTime, task.fullTime)
            roadToTimer.setOnClickListener { onTimerClickListener(task) }
        }
    }

    class DaysViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val today: TextView = itemView.findViewById(R.id.today)
        val lookAll: Button = itemView.findViewById(R.id.look_all)

        fun bind(onLookAllClickListener: () -> Unit) {
            lookAll.setOnClickListener { onLookAllClickListener() }
        }
    }

    private fun filterDate(holder: DaysViewHolder) {
        val newFilter = if (listWorker.currentFilter == TaskFilterAdapter.Today) {
            TaskFilterAdapter.AllTasks
        } else {
            TaskFilterAdapter.Today
        }
        listWorker.setFilter(newFilter)
        holder.today.text = holder.itemView.context.getString(
            if (newFilter == TaskFilterAdapter.Today) R.string.today else R.string.all_tasks
        )
        holder.lookAll.text = holder.itemView.context.getString(
            if (newFilter == TaskFilterAdapter.Today) R.string.view_today_tasks else R.string.look_all
        )
        notifyDataSetChanged()
    }

    fun updateTasksTime(taskRun: TaskRun) {
        val updatedTasks = listener.updateTimeTasks(listWorker.data, taskRun)
        listWorker.updateData(updatedTasks)
        val position = listWorker.data.indexOfFirst { it.id == taskRun.id }
        notifyItemChanged(position)
    }

    private fun setPlayButtonListener(position: Int, holder: AllTaskViewHolder) {
        listener.startTask(listWorker.data[position])
        val updatedTasks = listener.updateListTasks(listWorker.data)
        listWorker.updateData(updatedTasks)
        listWorker.data[position].state =
            holder.itemView.context.getString(State.InProgress.localState)
        notifyDataSetChanged()
    }

    private fun clickToTimer(position: Int) {
        listener.clickToTimer(listWorker.data[position], listWorker.data)
    }

    companion object {
        private const val RUNNING_TASK_VIEW_TYPE = 0
        private const val DAYS_VIEW_TYPE = 1
        private const val ALL_TASK_VIEW_TYPE = 2
    }
}