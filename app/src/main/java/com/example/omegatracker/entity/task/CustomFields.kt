package com.example.omegatracker.entity.task

import com.squareup.moshi.Json

data class CustomFields(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "value")
    val value: Value?
)