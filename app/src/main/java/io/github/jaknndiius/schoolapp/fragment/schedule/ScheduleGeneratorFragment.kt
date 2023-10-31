package io.github.jaknndiius.schoolapp.fragment.schedule

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.fragment.ScheduleFragment
import io.github.jaknndiius.schoolapp.view.ListViewAdapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ScheduleGeneratorFragment(
    val scheduleFragment: ScheduleFragment
) : Fragment() {

    private lateinit var binding: View

    private val MAX_CLASS_NUMBER = 10

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflater.inflate(R.layout.schedule_generate_schedule, container, false)
        binding.findViewById<NumberPicker>(R.id.number_picker).apply {
            minValue = 0
            maxValue = MAX_CLASS_NUMBER -1
            value = 0
            displayedValues = (1..MAX_CLASS_NUMBER).map { "${it}교시" }.toTypedArray()
        }
        binding.findViewById<DatePicker>(R.id.date_picker).apply {
            //TODO: 스케쥴 설정에 뒤로가기 버튼 추가하기
        }
        return binding
    }

}