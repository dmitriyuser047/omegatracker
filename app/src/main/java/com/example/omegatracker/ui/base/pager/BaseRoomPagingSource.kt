package com.example.omegatracker.ui.base.pager

import androidx.paging.PagingSource
import androidx.paging.PagingState

private const val BASE_STARTING_PAGE_INDEX = 1

abstract class BaseRoomPagingSource<Value : Any>(
    private val query: suspend (Int, Int) -> List<Value>
) : PagingSource<Int, Value>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Value> {
        val position = params.key ?: BASE_STARTING_PAGE_INDEX
        val page = params.key ?: BASE_STARTING_PAGE_INDEX
        val pageSize = params.loadSize
        val offset = (page - 1) * pageSize

        return try {
            val data = query(pageSize, offset)
            println(data.size)
            LoadResult.Page(
                data = data,
                prevKey = if (position == BASE_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (data.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Value>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}