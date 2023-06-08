package io.github.jaknndiius.schoolapp.fragment

import android.icu.util.LocaleData
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.R
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Date

class TimetableFragment() : Fragment() {
    private val weekday = listOf("월", "화", "수", "목", "금")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_timetable, container, false)
        val scrollView: HorizontalScrollView = binding.findViewById(R.id.tables_scroll)
        // 700 * n
        val tablesLayout: LinearLayout= scrollView.findViewById(R.id.tables_layout)

        for(d in weekday) {
            tablesLayout.addView(makeDay(inflater, tablesLayout, d + "요일"))
        }
        return binding
    }

    private fun getDay(): Int {
        // Sunday:0 ~ Saturday: 6
        return Date().day
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public fun reload() {
        val dayNumber = getDay().coerceAtMost(5).coerceAtLeast(1)
        this.view?.findViewById<HorizontalScrollView>(R.id.tables_scroll)?.scrollX = 700 * (dayNumber - 1)
    }

    private val t = listOf("수학1","B","C","D","E","F","G")
    private fun makeDay(inflater: LayoutInflater, container: ViewGroup, dayName: String): View {
        val view = inflater.inflate(R.layout.fragment_timetable_day, container, false)
        view.findViewById<TextView>(R.id.day_name).text = dayName
        val subjectLayout: LinearLayout = view.findViewById(R.id.subject_layout)
        for(i in 1..7) {
            subjectLayout.addView(
                makeSubject(inflater, subjectLayout, t[i-1], "윤동희")
            )
        }
        return view
    }
    private fun makeSubject(inflater: LayoutInflater, container: ViewGroup, subjectName: String, teacherName: String): View {
        return inflater.inflate(R.layout.fragment_timetable_subject, container, false).apply {
            findViewById<TextView>(R.id.subject_name).text = subjectName
            findViewById<TextView>(R.id.teacher_name).text = teacherName
        }
    }
}