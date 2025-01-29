package com.example.omegatracker.ui.history

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.omegatracker.R
import com.example.omegatracker.databinding.HistoryDateItemBinding
import com.example.omegatracker.databinding.HistoryItemBinding
import com.example.omegatracker.entity.HistoryItem
import com.example.omegatracker.utils.formatTimeLong
import com.example.omegatracker.utils.toSimpleString
import com.omega_r.libs.omegarecyclerview.pagination.PaginationViewCreator
import com.omega_r.libs.omegarecyclerview.sticky_decoration.StickyAdapter
import java.util.Date

interface HistoryFragmentListener {
    fun clickStickyHeader(position: Int)
    fun getDayOfTheWeek(date: Date): String
    fun onRetryClicked()
    fun getStartOfDay(date: Date): Long
}

class HistoryFragmentAdapter(
    private val listener: HistoryFragmentListener,
) :
    StickyAdapter<HistoryFragmentAdapter.HeaderHolder>,
    PaginationViewCreator,
    PagingDataAdapter<HistoryItem, HistoryFragmentAdapter.TaskViewHolder>(HISTORY) {

    companion object {
        const val PAGE_SIZE = 3
        val HISTORY = object : DiffUtil.ItemCallback<HistoryItem>() {
            override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
                return oldItem.historyTaskId == newItem.historyTaskId
            }

            override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
                return oldItem.historyTaskId == newItem.historyTaskId
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = HistoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val history = getItem(position)
        if (history != null) {
            holder.bind(history)
        }
    }

    override fun onCreateStickyViewHolder(parent: ViewGroup?): HeaderHolder {
        val binding =
            HistoryDateItemBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return HeaderHolder(binding)
    }

    override fun getStickyId(position: Int): Long {
        val historyItem = getItem(position)
        return if (historyItem != null) {
            listener.getStartOfDay(historyItem.date)
        } else {
            -1L
        }
    }

    override fun onClickStickyViewHolder(id: Long) {
        val position = snapshot().indexOfFirst { listener.getStartOfDay(it!!.date) == id }
        if (position != -1) {
            listener.clickStickyHeader(position)
        }
    }

    override fun onBindStickyViewHolder(viewHolder: HeaderHolder?, position: Int) {
        val historyItem = getItem(position)
        if (viewHolder != null && historyItem != null) {
            viewHolder.bind(historyItem)
        } else {
            Log.e("HistoryFragmentAdapter", "Error binding sticky header at position $position")
        }
    }

    override fun createPaginationView(parent: ViewGroup?, inflater: LayoutInflater?): View? {
        return inflater?.inflate(R.layout.item_progress, parent, false)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun createPaginationErrorView(parent: ViewGroup?, inflater: LayoutInflater?): View {
        val view = inflater?.inflate(R.layout.item_error_loading, parent, false)
        val textError: TextView = view!!.findViewById(R.id.text_error)
            textError.setOnClickListener {
                listener.onRetryClicked()
            }
        return view
    }

    inner class TaskViewHolder(private val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: HistoryItem) {
            binding.taskName.text = task.historyTaskName
            binding.nameProject.text = task.historyTaskProject
            binding.taskIcon.setImageResource(R.drawable.icon_monitor_circle)
            binding.timeStartValue.text = formatTimeLong(task.startTime)
            binding.timeEndValue.text = formatTimeLong(task.endTime)
        }
    }

    inner class HeaderHolder(private val binding: HistoryDateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryItem) {
            binding.day.text = listener.getDayOfTheWeek(item.date)
            binding.date.text = toSimpleString(item.date)
        }
    }
}
