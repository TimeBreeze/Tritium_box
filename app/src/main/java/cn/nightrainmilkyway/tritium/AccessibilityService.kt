package cn.nightrainmilkyway.tritium

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat

class AccessibilityService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private val checkInterval: Long = 60000

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        scheduleServiceCheck()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onInterrupt() {

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        val channelId = "accessibility_service_channel"
        val channelName = "Accessibility Service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Accessibility Service")
            .setContentText("Service is running")
            .setSmallIcon(R.drawable.unkonw)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }

    private fun scheduleServiceCheck() {
        handler.postDelayed({
            if (!isServiceRunning(this, AccessibilityService::class.java)) {
                startService(this)
            }
            scheduleServiceCheck()
        }, checkInterval)
    }

    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    companion object {
        fun startService(context: Context) {
            val intent = Intent(context, AccessibilityService::class.java)
            context.startService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, AccessibilityService::class.java)
            context.stopService(intent)
        }
    }
}