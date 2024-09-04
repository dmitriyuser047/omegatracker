package com.example.omegatracker.entity

import android.os.Parcelable
import com.example.omegatracker.ui.Screens
import kotlinx.parcelize.Parcelize

@Parcelize
data class NavigationData(
    val screen: Screens,
    val info: String?
): Parcelable
