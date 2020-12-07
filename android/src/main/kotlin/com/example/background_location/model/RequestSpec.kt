package com.example.background_location.model

data class RequestSpec(
    val interval: Int = 5000,
    val priority: String = "balanced"
)