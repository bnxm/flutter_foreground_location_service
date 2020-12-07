package com.example.background_location

import android.app.*
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.background_location.model.NotificationSpec
import com.example.background_location.model.RequestSpec
import com.example.background_location.util.getNamedDrawable
import com.example.background_location.util.toGson
import io.flutter.plugin.common.MethodChannel

class LocationService : Service() {

    companion object {
        const val NOTIFICATION_CHANNEL = "location_channel"

        var channel: MethodChannel? = null
        var requestSpec = RequestSpec()
    }

    var notificationSpec: NotificationSpec = NotificationSpec()
        set(value) {
            field = value
            unregisterActionReceiver()
            registerActionReceiver()
            updateNotification()
        }

    var activity: Activity? = null
        set(value) {
            field = value
            updateNotification()
        }

    private val provider by lazy { LocationProviderImpl(this) }
    private val binder = ServiceBinder()

    private var receiver: NotificationActionReceiver? = null

    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateNotification()

        provider.start(requestSpec) {
            channel?.invokeMethod("location", it.toGson())
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        provider.stop()
        unregisterActionReceiver()
    }

    private fun updateNotification() {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL).run {
            setShowWhen(false)
            setContentTitle(notificationSpec.title)
            setContentText(notificationSpec.text)
            setStyle(NotificationCompat.BigTextStyle().run {
                if (notificationSpec.summary.isNotBlank())
                    setSummaryText(notificationSpec.summary)
                setBigContentTitle(notificationSpec.title)
            })

            setColor(Color.parseColor(notificationSpec.color))
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setSmallIcon(getNamedDrawable(notificationSpec.icon))

            notificationSpec.actions.forEachIndexed { index, action ->
                val intent = Intent(this@LocationService, NotificationActionReceiver::class.java).apply {
                    this.action = action.name
                }

                val pendingIntent = PendingIntent.getBroadcast(this@LocationService, index + 10, intent, 0)
                addAction(R.drawable.ic_location, action.name, pendingIntent)
            }

            if (activity != null) {
                val intent = Intent(this@LocationService, activity!!::class.java)
                val pending = PendingIntent.getActivity(this@LocationService, 2, intent, 0)
                setContentIntent(pending)
            }

            build()
        }

        startForeground(1, notification)
    }

    private fun registerActionReceiver() {
        if (receiver != null) return

        val filter = IntentFilter().apply {
            for (action in notificationSpec.actions) {
                addAction(action.name)
            }
        }

        receiver = NotificationActionReceiver(channel ?: return)
        registerReceiver(receiver, filter)
    }

    private fun unregisterActionReceiver() {
        if (receiver == null) return

            unregisterReceiver(receiver)
        receiver = null
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL,
                "Location Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    inner class ServiceBinder : Binder() {
        val service: LocationService
            get() = this@LocationService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }
}