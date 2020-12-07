package com.example.background_location.model

data class NotificationSpec(
    val title: String = "Location Service",
    val text: String = "Location Service is running...",
    val summary: String = "",
    val icon: String = "ic_location",
    val color: String = "#FF0000",
    val actions: List<NotificationAction> = emptyList()
)

data class NotificationAction(
    val icon: String,
    val name: String
)