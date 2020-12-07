package com.example.background_location.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> String.toObject(): T {
    val gson = Gson()
    val type = object : TypeToken<T>() {}.type
    return gson.fromJson(this, type)
}

inline fun <reified T> T.toGson(): String {
    val gson = Gson()
    val type = object : TypeToken<T>() {}.type
    return gson.toJson(this, type)
}