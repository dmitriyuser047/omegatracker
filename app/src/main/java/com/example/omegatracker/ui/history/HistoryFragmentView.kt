package com.example.omegatracker.ui.history

import com.example.omegatracker.entity.HistoryItem
import com.example.omegatracker.ui.base.activity.BaseView

interface HistoryFragmentView: BaseView {
    fun getHistoryItems(historyTasks: List<HistoryItem>)
}