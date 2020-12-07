package com.example.background_location

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import com.example.background_location.model.NotificationSpec
import com.example.background_location.util.toObject
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import java.lang.Exception

class BackgroundLocationPlugin : FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
        private const val SERVICE_REQUEST_CODE = 2
    }

    private lateinit var channel: MethodChannel
    private lateinit var context: Context

    private var activity: Activity? = null

    private var service: LocationService? = null
    private var serviceConnection: ServiceConnection? = null
    private var notificationSpec = NotificationSpec()

    private var result: Result? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "background_location")
        channel.setMethodCallHandler(this)

        context = flutterPluginBinding.applicationContext
        LocationService.channel = channel
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        this.result = result

        val args = call.arguments as String?

        when (call.method) {
            "is_service_enabled" -> result.success(getServiceStatus())
            "request_service" -> requestService()
            "permission_status" -> result.success(getPermissionStatus())
            "request_permission" -> requestPermission()
            "start" -> startService(args!!)
            "stop" -> stopService()
            "notification" -> {
                notificationSpec = args!!.toObject()
                service?.notificationSpec = notificationSpec
            }
        }
    }

    private fun getServiceStatus(): String {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return when {
            manager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> "gps"
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> "network"
            else -> "disabled"
        }
    }

    private fun requestService() {
        val request = LocationSettingsRequest.Builder()
            .addLocationRequest(
                LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            )
            .build()

        LocationServices
            .getSettingsClient(activity!!)
            .checkLocationSettings(request)
            .addOnFailureListener {
                if (it is ResolvableApiException) {
                    try {
                        it.startResolutionForResult(activity!!, SERVICE_REQUEST_CODE);
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
    }

    private fun getPermissionStatus(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val status = ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_BACKGROUND_LOCATION)

            if (status == PackageManager.PERMISSION_GRANTED) {
                return "always_granted"
            }
        }

        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val isGranted = ActivityCompat.checkSelfPermission(activity!!, permission) == PackageManager.PERMISSION_GRANTED

        return if (!isGranted && ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)) {
            "permanently_denied"
        } else if (isGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) "granted_in_use" else "always_granted"
        } else {
            "denied"
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
    }

    private fun startService(args: Any) {
        LocationService.requestSpec = (args as String).toObject()

        val intent = Intent(context, LocationService::class.java)
        context.startService(intent)

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as LocationService.ServiceBinder
                val plugin = this@BackgroundLocationPlugin

                plugin.service = binder.service.apply {
                    notificationSpec = plugin.notificationSpec
                    activity = plugin.activity
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                this@BackgroundLocationPlugin.service = null
            }
        }

        context.bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)
    }

    private fun stopService() {
        if (serviceConnection != null)
            context.unbindService(serviceConnection!!)

        val intent = Intent(context, LocationService::class.java)
        context.stopService(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return when(requestCode) {
            SERVICE_REQUEST_CODE -> {
                result?.success(getPermissionStatus())
                true
            }
            else -> false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?): Boolean {
        return when(requestCode) {
            PERMISSION_REQUEST_CODE -> {
                result?.success(getPermissionStatus())
                true
            }
            else -> false
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)

        stopService()
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity

        binding.addActivityResultListener(this)
        binding.addRequestPermissionsResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        activity = null
    }
}