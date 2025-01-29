package com.example.omegatracker.ui.history

import androidx.paging.PagingData
import com.example.omegatracker.data.RepositoryImpl
import com.example.omegatracker.entity.HistoryItem
import com.example.omegatracker.ui.base.activity.BasePresenter
import com.example.omegatracker.ui.history.HistoryFragmentAdapter.Companion.PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class HistoryFragmentPresenter @Inject constructor(private val repository: RepositoryImpl) :
    BasePresenter<HistoryFragmentView>() {

    fun loadItems(): Flow<PagingData<HistoryItem>> {
        return repository.getHistoryData(PAGE_SIZE)
    }

    fun getDayOfTheWeek(date: Date): String {
        return repository.getDayOfWeek(date)
    }

    fun getStartOfDayInMillis(date: Date): Long {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}