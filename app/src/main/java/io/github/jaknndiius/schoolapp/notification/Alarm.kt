package io.github.jaknndiius.schoolapp.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import java.util.Calendar

class Alarm(
    private val context: Context
) {

    private val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val REQUEST_CODE = 212
    }

    fun alert() {

        val pendingIntent = Intent(context, ScheduleAlarmReceiver::class.java).let {
            it.putExtra("code", REQUEST_CODE)
            PendingIntent.getBroadcast(context, REQUEST_CODE, it, PendingIntent.FLAG_IMMUTABLE)
        }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 22)
            set(Calendar.MINUTE, 21)
        }

        manager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}