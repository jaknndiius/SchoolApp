package io.github.jaknndiius.schoolapp.fragment.schedule

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.animation.CustomAnimation
import io.github.jaknndiius.schoolapp.database.Schedule
import io.github.jaknndiius.schoolapp.fragment.ScheduleFragment
import io.github.jaknndiius.schoolapp.fragment.objects.BackPressableFragment
import io.github.jaknndiius.schoolapp.preset.Direction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ScheduleModifierFragment(
    private val scheduleFragment: ScheduleFragment,
    private val schedule: Schedule
) : BackPressableFragment() {

    private lateinit var binding: View

    private val MAX_CLASS_NUMBER = 10

    override fun onPressBack() {
        val title = binding.findViewById<EditText>(R.id.schedule_title).text.toString()
        val detail =  binding.findViewById<EditText>(R.id.detail_eidttext).text.toString()
        if(title.isNotBlank() || detail.isNotBlank()) {
            AlertDialog.Builder(context)
                .setMessage(R.string.ask_really_cancel_modifying_schedule)
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    scheduleFragment.openScheduleList(Direction.PREVIOUS_VERTICAL)
                }
                .setNegativeButton(getString(R.string.no),  null)
                .show()
        } else scheduleFragment.openScheduleList(Direction.PREVIOUS_VERTICAL)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflater.inflate(R.layout.schedule_generate_schedule, container, false)

        binding.findViewById<TextView>(R.id.setting_title).setText(R.string.setting_modifying_schedule)

        binding.findViewById<Button>(R.id.back_button).setOnClickListener {
            onPressBack()
        }

        val titleView = binding.findViewById<EditText>(R.id.schedule_title)
        titleView.setText(schedule.name)

        val detailView = binding.findViewById<EditText>(R.id.detail_eidttext)
        detailView.setText(schedule.detail
        )
        val numberPicker = binding.findViewById<NumberPicker>(R.id.number_picker).apply {
            minValue = 0
            maxValue = MAX_CLASS_NUMBER -1
            value = schedule.classNumber -1
            displayedValues = (1..MAX_CLASS_NUMBER).map { "${it}교시" }.toTypedArray()
        }

        val datePicker = binding.findViewById<DatePicker>(R.id.date_picker).apply {
            schedule.date.toString().chunked(2).let {
                updateDate((it[0] + it[1]).toInt(), it[2].toInt() -1, it[3].toInt())
            }
        }

        binding.findViewById<AppCompatButton>(R.id.cancel_button).apply {
            attachTouchAnimation(this)
            setOnClickListener {
                onPressBack()
            }
        }

        binding.findViewById<AppCompatButton>(R.id.save_button).apply {
            attachTouchAnimation(this)
            setOnClickListener {
                val title = titleView.text.toString()
                if(title.isBlank()) {
                    Toast.makeText(context, R.string.request_input_subject_name, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val classNumber = numberPicker.value +1
                val detail = detailView.text.toString()
                val date = datePicker.run { "$year${"%02d".format(month +1 )}${"%02d".format(dayOfMonth)}${"%02d".format(classNumber)}" }.toLong()
                val displayDate = datePicker.run {"${month+1}월 ${dayOfMonth}일" }

                CoroutineScope(Dispatchers.IO).launch {

                    MainActivity.scheduleManager.modify(
                        Schedule(schedule.id, title, detail, date, displayDate, classNumber)
                    )

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context,
                            R.string.say_modified_schedule,
                            Toast.LENGTH_LONG).show()
                        scheduleFragment.openScheduleList(Direction.PREVIOUS_VERTICAL)
                    }
                }

            }
        }

        return binding
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun attachTouchAnimation(view: View) {
        view.setOnTouchListener { v, e ->
            when(e.action) {
                MotionEvent.ACTION_DOWN -> {
                    context?.let { CustomAnimation.changeColor(it, v, R.color.white, R.color.light_gray) }
                }
                MotionEvent.ACTION_UP -> {
                    context?.let { CustomAnimation.changeColor(it, v, R.color.light_gray, R.color.white) }
                }
            }
            false
        }
    }

}