package com.example.omegatracker.ui.statistics

import com.example.omegatracker.data.RepositoryImpl
import com.example.omegatracker.db.entity.HistoryData
import com.example.omegatracker.entity.Periods
import com.example.omegatracker.ui.base.activity.BasePresenter
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

class StatisticsFragmentPresenter @Inject constructor(private val repository: RepositoryImpl) :
    BasePresenter<StatisticsFragmentView>() {

    fun setData(period: Periods) {
        launch {
            when (period) {
                Periods.DAY -> {
                    val getData = repository.getDataBetweenDate(getTimeForPeriod(period))
                    viewState.getStatistics(getData.size, sumTimesHistoryTasks(getData))
                    viewState.getStatisticsForGraph(getData, period)
                }
                Periods.WEEK -> {
                    val getData = repository.getDataBetweenDate(getTimeForPeriod(period))
                    viewState.getStatistics(getData.size, sumTimesHistoryTasks(getData))
                    viewState.getStatisticsForGraph(getData, period)
                }
            }
        }
    }

    private fun sumTimesHistoryTasks(historyData: List<HistoryData>): Pair<String, String> {
        val time = historyData.sumOf { it.endTime - it.startTime }
        val hours = (time / (1000 * 60 * 60)).toInt().toString()
        val minutes = ((time / (1000*60))%60).toInt().toString()
        return Pair(hours, minutes)
    }

    private fun getTimeForPeriod(period: Periods): Pair<Long, Long> {
            val calendar = Calendar.getInstance()
            val (startOfPeriod, endOfPeriod) = when (period) {
                Periods.DAY -> {
                    calendar.apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis to
                            calendar.apply {
                                set(Calendar.HOUR_OF_DAY, 23)
                                set(Calendar.MINUTE, 59)
                                set(Calendar.SECOND, 59)
                                set(Calendar.MILLISECOND, 999)
                            }.timeInMillis
                }
                Periods.WEEK -> {
                    calendar.apply {
                        firstDayOfWeek = Calendar.MONDAY
                        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis to
                            calendar.apply {
                                firstDayOfWeek = Calendar.MONDAY
                                set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                                set(Calendar.HOUR_OF_DAY, 23)
                                set(Calendar.MINUTE, 59)
                                set(Calendar.SECOND, 59)
                                set(Calendar.MILLISECOND, 999)
                            }.timeInMillis
                }
            }
            return Pair(startOfPeriod, endOfPeriod)
        }
}