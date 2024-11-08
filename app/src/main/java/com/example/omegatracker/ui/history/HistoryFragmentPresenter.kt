package com.example.omegatracker.ui.history

import com.example.omegatracker.data.RepositoryImpl
import com.example.omegatracker.entity.HistoryItem
import com.example.omegatracker.ui.base.activity.BasePresenter
import kotlinx.coroutines.launch
import javax.inject.Inject

class HistoryFragmentPresenter @Inject constructor(private val repository: RepositoryImpl) :
    BasePresenter<HistoryFragmentView>() {
    init {
        launch {
            val tasks = repository.getHistoryTasks()
            viewState.getHistoryItems(sortTasks(tasks))
        }
    }

    private fun sortTasks(historyItems: List<HistoryItem>): List<HistoryItem> {
        val sortedItems = historyItems.sortedByDescending { it.date }
        println(sortedItems)
        return sortedItems
    }
}