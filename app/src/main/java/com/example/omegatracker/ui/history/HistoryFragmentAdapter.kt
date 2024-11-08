package com.example.omegatracker.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.omegatracker.R
import com.example.omegatracker.databinding.HistoryDateItemBinding
import com.example.omegatracker.databinding.HistoryItemBinding
import com.example.omegatracker.entity.HistoryItem
import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView
import com.omega_r.libs.omegarecyclerview.sticky_decoration.StickyAdapter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

interface HistoryFragmentListener {
    fun clickStickyHeader(position: Int)
}

class HistoryFragmentAdapter(private val historyItems: List<HistoryItem>, private val listener: HistoryFragmentListener) :
    OmegaRecyclerView.Adapter<RecyclerView.ViewHolder>(),
    StickyAdapter<HistoryFragmentAdapter.HeaderHolder> {

    private val groupedItems: List<Any> = groupHistoryItemsByDate(historyItems)

    companion object {
        const val ITEM_TYPE_DATE = 0
        const val ITEM_TYPE_TASK = 1
    }

    private fun groupHistoryItemsByDate(items: List<HistoryItem>): List<Any> {
        val grouped = mutableListOf<Any>()
        var currentDate: String? = null

        for (item in items) {
            if (currentDate != item.date) {
                currentDate = item.date
                grouped.add(item)
            }
            if (!grouped.contains(item)) {
                grouped.add(item)
            }
        }

        return grouped
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_DATE -> {
                val binding = HistoryDateItemBinding.inflate(layoutInflater, parent, false)
                HeaderHolder(binding)
            }
            ITEM_TYPE_TASK -> {
                val binding = HistoryItemBinding.inflate(layoutInflater, parent, false)
                TaskViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TaskViewHolder -> holder.bind(groupedItems[position] as HistoryItem)
            is HeaderHolder -> holder.bind(groupedItems[position] as HistoryItem)
        }
    }

    override fun getItemCount(): Int = groupedItems.size

    override fun getItemViewType(position: Int): Int {
        return when (groupedItems[position]) {
            is HistoryItem -> if ((groupedItems[position] as HistoryItem).historyTaskName == "Header") ITEM_TYPE_DATE else ITEM_TYPE_TASK
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun getStickyId(position: Int): Long {
        return if (groupedItems[position] is HistoryItem) {
            (groupedItems[position] as HistoryItem).date.hashCode().toLong()
        } else {
            -1L
        }
    }

    override fun onCreateStickyViewHolder(parent: ViewGroup?): HeaderHolder {
        val binding = HistoryDateItemBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return HeaderHolder(binding)
    }

    override fun onClickStickyViewHolder(id: Long) {
        val position = groupedItems.indexOfFirst {
            it is HistoryItem && it.date.hashCode().toLong() == id
        }
        println(position)
        if (position - 1 > groupedItems.size) {
            listener.clickStickyHeader(position - 1)
        } else listener.clickStickyHeader(position)
    }

    override fun onBindStickyViewHolder(viewHolder: HeaderHolder?, position: Int) {
        if (viewHolder != null && groupedItems[position] is HistoryItem) {
            viewHolder.bind(groupedItems[position] as HistoryItem)
        }
    }

    private fun formatTime(timestamp: Long): String {
        val zonedDateTime = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            ZoneId.systemDefault()
        )
        return DateTimeFormatter.ofPattern("HH:mm").format(zonedDateTime)
    }

    private fun getDayOfWeek(date: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val localDate = LocalDate.parse(date, formatter)
        return localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }

    inner class TaskViewHolder(private val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: HistoryItem) {
            binding.taskName.text = task.historyTaskName
            binding.nameProject.text = task.historyTaskProject
            binding.taskIcon.setImageResource(R.drawable.icon_monitor_circle)
            binding.timeStartValue.text = formatTime(task.startTime)
            binding.timeEndValue.text = formatTime(task.endTime)
        }
    }

    inner class HeaderHolder(private val binding: HistoryDateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryItem) {
            binding.day.text = getDayOfWeek(item.date)
            binding.date.text = item.date
        }
    }
}

//class HistoryFragmentAdapter(historyItems: List<HistoryItem>) : OmegaRecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//
//    private val groupedTasks = historyItems.groupBy { it.date }
//
//    companion object {
//        const val ITEM_TYPE_DATE = 0
//        const val ITEM_TYPE_TASK = 1
//    }
//
//    override fun getItemCount(): Int {
//        val count = groupedTasks.keys.sumOf { (groupedTasks[it]?.size ?: 0) + 1 }
//        return count
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        var currentIndex = 0
//        for (date in groupedTasks.keys) {
//            if (currentIndex == position) {
//                return ITEM_TYPE_DATE
//            }
//            currentIndex++
//            val tasks = groupedTasks[date] ?: emptyList()
//            if (position in currentIndex until currentIndex + tasks.size) {
//                return ITEM_TYPE_TASK
//            }
//            currentIndex += tasks.size
//        }
//        throw IllegalArgumentException("Invalid view type")
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            ITEM_TYPE_DATE -> {
//                val binding = HistoryDateItemBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//                DateViewHolder(binding)
//            }
//            ITEM_TYPE_TASK -> {
//                val binding = HistoryItemBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//                TaskViewHolder(binding)
//            }
//            else -> throw IllegalArgumentException("Invalid view type")
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        var currentIndex = 0
//        for (date in groupedTasks.keys) {
//            if (currentIndex == position) {
//                holder as DateViewHolder
//                holder.bind(date, getDayOfWeek(date))
//                return
//            }
//            currentIndex++
//            val tasks = groupedTasks[date] ?: emptyList()
//            if (position in currentIndex until currentIndex + tasks.size) {
//                holder as TaskViewHolder
//                holder.bind(tasks[position - currentIndex])
//                return
//            }
//            currentIndex += tasks.size
//        }
//    }
//
//    inner class DateViewHolder(private val binding: HistoryDateItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(date: String, day: String) {
//            binding.date.text = date
//            binding.day.text = day
//        }
//    }
//
//    inner class TaskViewHolder(private val binding: HistoryItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(task: HistoryItem) {
//            binding.taskName.text = task.historyTaskName
//            binding.nameProject.text = task.historyTaskProject
//            binding.taskIcon.setImageResource(R.drawable.icon_monitor_circle)
//            binding.timeStartValue.text = formatTime(task.startTime)
//            binding.timeEndValue.text = formatTime(task.endTime)
//        }
//    }
//

//}