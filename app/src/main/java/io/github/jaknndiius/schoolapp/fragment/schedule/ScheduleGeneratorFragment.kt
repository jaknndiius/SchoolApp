package io.github.jaknndiius.schoolapp.fragment.schedule

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

class ScheduleGeneratorFragment(
    private val scheduleFragment: ScheduleFragment
) : BackPressableFragment() {

    private lateinit var binding: View

    private val MAX_CLASS_NUMBER = 10

    override fun onPressBack() {
        val title = binding.findViewById<EditText>(R.id.schedule_title).text.toString()
        val detail =  binding.findViewById<EditText>(R.id.detail_eidttext).text.toString()
        if(title.isNotBlank() || detail.isNotBlank()) {
            AlertDialog.Builder(context)
                .setMessage("일정 추가를 취소하시겠습니까?")
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    scheduleFragment.openScheduleList(Direction.PREVIOUS_VERTICAL)
                }
                .setNegativeButton(getString(R.string.no),  null)
                .show()
        } else scheduleFragment.openScheduleList(Direction.PREVIOUS_VERTICAL)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflater.inflate(R.layout.schedule_generate_schedule, container, false)

        binding.findViewById<Button>(R.id.back_button).setOnClickListener {
            onPressBack()
        }

        val titleView = binding.findViewById<EditText>(R.id.schedule_title)
        val detailView = binding.findViewById<EditText>(R.id.detail_eidttext)

        val numberPicker = binding.findViewById<NumberPicker>(R.id.number_picker).apply {
            minValue = 0
            maxValue = MAX_CLASS_NUMBER -1
            value = 0
            displayedValues = (1..MAX_CLASS_NUMBER).map { "${it}교시" }.toTypedArray()
        }
        val datePicker = binding.findViewById<DatePicker>(R.id.date_picker)

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
                    Toast.makeText(context, "과목이름을 입력해 주세요.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                val detail = detailView.text.toString()
                val date = datePicker.run {"$year-${month+1}-$dayOfMonth" }
                val displayDate = datePicker.run {"${month+1}월 ${dayOfMonth}일" }
                val classNumber = numberPicker.value +1

                CoroutineScope(Dispatchers.IO).launch {
                    if(MainActivity.scheduleManager.isExist(title)) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "이미 존재하는 이름입니다.", Toast.LENGTH_LONG).show()
                        }
                        return@launch
                    }
                    MainActivity.scheduleManager.define(
                        Schedule(title, detail, date, displayDate, classNumber)
                    )
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "일정을 추가했습니다.", Toast.LENGTH_LONG).show()
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