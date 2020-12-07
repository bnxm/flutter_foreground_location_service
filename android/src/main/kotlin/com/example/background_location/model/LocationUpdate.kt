package com.example.background_location.model

import androidx.annotation.Keep

@Keep
data class LocationUpdate(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val velocity: Double,
    val bearing: Double,
    val time: Long
)