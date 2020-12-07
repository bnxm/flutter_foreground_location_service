package com.example.background_location.util

import android.content.Context

fun Context.getNamedDrawable(name: String) : Int {
    return resources.getIdentifier(name, "drawable", packageName)
}