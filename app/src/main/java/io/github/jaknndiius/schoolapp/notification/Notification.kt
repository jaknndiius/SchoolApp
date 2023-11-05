package io.github.jaknndiius.schoolapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class Notification(
    private val context: Context
) {

    private val ID = "channel1"
    private val NAME = "Channel1"

    private val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun notify(title: String, text: String, id: Int = 1) {
        manager.notify(
            id,
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.createNotificationChannel(NotificationChannel(
                    ID, NAME, NotificationManager.IMPORTANCE_DEFAULT
                ))
                NotificationCompat.Builder(context, ID)
            } else {
                NotificationCompat.Builder(context)
            }.apply {
                setContentTitle(title)
                setContentText(text)
                setSmallIcon(android.R.drawable.ic_menu_view)
            }.build()
        )
    }

}