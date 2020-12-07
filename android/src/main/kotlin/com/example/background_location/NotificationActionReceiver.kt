package com.example.background_location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import io.flutter.plugin.common.MethodChannel

class NotificationActionReceiver(
    private val channel: MethodChannel?
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "hlkj", Toast.LENGTH_SHORT).show()
        if (intent == null) return

        channel?.invokeMethod("action", intent.action)
    }
}