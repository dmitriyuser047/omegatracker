package com.example.omegatracker.entity


import com.example.omegatracker.R
import com.example.omegatracker.di.TYPE
import com.squareup.moshi.Json

enum class State(
    val localState: Int,
) {
    InProgress(R.string.in_progress),
    Open(R.string.open),
    InPause(R.string.pause),
    Stopped(R.string.stopped)
}

sealed class Value {
    data class StateValue(
        @Json(name = TYPE)
        val type: String,
        @Json(name = "name")
        val name: String?
    ): Value()
    data class PeriodValue(
        @Json(name = TYPE)
        val type: String,
        @Json(name = "minutes")
        val minutes: Int?
    ): Value()
}