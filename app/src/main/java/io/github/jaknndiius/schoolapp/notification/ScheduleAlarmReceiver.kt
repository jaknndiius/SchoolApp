package io.github.jaknndiius.schoolapp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.jaknndiius.schoolapp.MainActivity

class ScheduleAlarmReceiver: BroadcastReceiver() {
    companion object {
        var aa = 1
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.extras?.get("code") == Alarm.REQUEST_CODE) {
            aa++
            MainActivity.notificationManager.notify(
                aa.toString(),
                "도착했어요!",
                aa
            )
            Log.d("FFFFFFFFFFFFFFFFFFF", aa.toString())
        }
    }
}