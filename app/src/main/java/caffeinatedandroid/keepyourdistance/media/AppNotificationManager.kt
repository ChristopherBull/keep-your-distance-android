package caffeinatedandroid.keepyourdistance.media

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import caffeinatedandroid.keepyourdistance.MainActivity
import caffeinatedandroid.keepyourdistance.R
import caffeinatedandroid.keepyourdistance.util.SingletonHolder


/**
 * Manages Notifications for this application. Can create and show [Notification]s.
 *
 * Centralises boilerplate code for the generation of [Notification]s.
 */
class AppNotificationManager private constructor(context: Context) {

    companion object : SingletonHolder<AppNotificationManager, Context>(::AppNotificationManager) {
        private const val NOTIFICATION_CHANNEL_ID_SERVICE_RUNNING = "proximity_service"
        private const val NOTIFICATION_CHANNEL_ID_PROXIMITY_ALERT = "proximity_alert"

        /**
         * ID for 'Service Running' [Notification]. Typically used when starting a foreground
         * service.
         */
        const val NOTIFICATION_ID_SERVICE_RUNNING: Int = 1
    }

    init {
        setupNotificationChannels(context)
    }

    private fun setupNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            with (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
                val name: CharSequence =
                    context.getString(R.string.notification_channel_name_detection_service)
                val descriptionText =
                    context.getString(R.string.notification_channel_description_detection_service)
                // Use priority `PRIORITY_LOW`. `PRIORITY_MIN` will result in Android showing a
                // notification saying the app is draining battery (as a deterrent).
                val importance = NotificationManager.IMPORTANCE_LOW
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID_SERVICE_RUNNING,
                    name,
                    importance
                ).apply {
                    description = descriptionText
                    setSound(null, null)
                    enableVibration(false)
                }
                createNotificationChannel(channel)
            }
        }
    }

    /**
     * Generates a [Notification] informing user that the service is running. Typically called
     * when starting a foreground service, which requires the [Notification] object directly.
     */
    fun notificationServiceRunning(context: Context): Notification {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_SERVICE_RUNNING).apply {
            setSmallIcon(R.drawable.ic_notification)
            setContentTitle(context.getString(R.string.notification_title_service_running))
            setContentText(context.getString(R.string.notification_text_service_running))
            // Use priority `PRIORITY_LOW`. `PRIORITY_MIN` will result in Android showing a
            // notification saying the app is draining battery (as a deterrent).
            priority = NotificationCompat.PRIORITY_LOW
            // Tap action
            val intent = Intent(context.applicationContext, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context.applicationContext, 0, intent, 0)
            setContentIntent(pendingIntent)
        }.build()
    }

    /**
     * Generates a [Notification] informing the user that a device has been detected nearby (within
     * their predefined threshold).
     */
    fun notificationDeviceDetected(context: Context): Notification {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_PROXIMITY_ALERT).apply {
            setSmallIcon(R.drawable.ic_notification)
            setContentTitle(context.getString(R.string.notification_title_device_detected))
            setContentText(context.getString(R.string.notification_text_device_detected))
            priority = NotificationCompat.PRIORITY_HIGH
        }.build()
    }
}
