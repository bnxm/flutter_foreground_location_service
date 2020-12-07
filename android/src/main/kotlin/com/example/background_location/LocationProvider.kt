package com.example.background_location

import android.content.Context
import com.example.background_location.model.LocationUpdate
import com.example.background_location.model.RequestSpec
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

interface LocationProvider {
    fun start(spec: RequestSpec, onUpdate: (location: LocationUpdate) -> Unit)
    fun stop()
}

class LocationProviderImpl(context: Context) : LocationProvider {

    private val flpc = LocationServices.getFusedLocationProviderClient(context)

    private var callback: LocationCallback? = null

    override fun start(spec: RequestSpec, onUpdate: (location: LocationUpdate) -> Unit) {
        val request = LocationRequest().apply {
            interval = spec.interval.toLong()
            maxWaitTime = spec.interval.toLong()
            priority = when(spec.priority) {
                "high" -> LocationRequest.PRIORITY_HIGH_ACCURACY
                "low_power" -> LocationRequest.PRIORITY_LOW_POWER
                "no_power" -> LocationRequest.PRIORITY_NO_POWER
                else -> LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            }
        }

        callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val update = result.lastLocation

                val location = LocationUpdate(
                    latitude = update.latitude,
                    longitude = update.longitude,
                    altitude = update.altitude,
                    velocity = update.speed.toDouble(),
                    bearing = update.bearing.toDouble(),
                    time = update.time
                )

                onUpdate(location)
            }
        }

        try {
            flpc.requestLocationUpdates(request, callback, null)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun stop() {
        if (callback != null) {
            flpc.removeLocationUpdates(callback)
        }
    }
}