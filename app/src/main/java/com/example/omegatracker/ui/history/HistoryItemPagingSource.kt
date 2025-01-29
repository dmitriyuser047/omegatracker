package com.example.omegatracker.ui.history

import com.example.omegatracker.db.dao.TaskDao
import com.example.omegatracker.entity.HistoryItem
import com.example.omegatracker.ui.base.pager.BaseRoomPagingSource

class HistoryItemSource(
    private val taskDao: TaskDao,
) : BaseRoomPagingSource<HistoryItem>({ pageSize, offset ->
    taskDao.getAllHistoryData(pageSize, offset)
})