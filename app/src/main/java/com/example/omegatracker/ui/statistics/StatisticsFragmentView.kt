package com.example.omegatracker.ui.statistics

import com.example.omegatracker.db.entity.HistoryData
import com.example.omegatracker.entity.Periods
import com.example.omegatracker.ui.base.activity.BaseView

interface StatisticsFragmentView: BaseView {
    fun getStatistics(tasksCount: Int, time: Pair<String, String>)
    fun getStatisticsForGraph(historyData: List<HistoryData>, periods: Periods)
}