package io.github.jaknndiius.schoolapp.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.database.Schedule
import io.github.jaknndiius.schoolapp.database.Subject
import io.github.jaknndiius.schoolapp.fragment.schedule.ListFragment
import io.github.jaknndiius.schoolapp.preset.Direction
import io.github.jaknndiius.schoolapp.preset.RangeType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleInfoDialog(
    context: Context,
    private val listFragment: ListFragment,
    private val schedule: Schedule
): AlertDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.schedule_info_dialog_layout)
        window?.setBackgroundDrawableResource(R.drawable.transparent)

        findViewById<TextView>(R.id.title).text = schedule.name
        findViewById<TextView>(R.id.subtitle).text = "${schedule.displayDate} ${schedule.classNumber}교시"
        findViewById<TextView>(R.id.details).text = schedule.detail

        findViewById<AppCompatButton>(R.id.delete_button).setOnClickListener {
            listFragment.adapter.deleteSchedule(schedule)
            dismiss()
        }
        findViewById<AppCompatButton>(R.id.modify_button).setOnClickListener {
            listFragment.scheduleFragment.openScheduleModifier(Direction.NEXT_VERTICAL, schedule)
            dismiss()
        }
    }
}