package io.github.jaknndiius.schoolapp.fragment.timetable

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.database.SubjectTable
import io.github.jaknndiius.schoolapp.fragment.TimetableFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ListFragment(
    private val timetableFragment: TimetableFragment
) : Fragment() {

    private val weekday = listOf("월", "화", "수", "목", "금")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = inflater.inflate(R.layout.timetable_list, container, false)
                val scrollView: HorizontalScrollView = binding.findViewById(R.id.tables_scroll)
        // 700 * n
        val tablesLayout: LinearLayout = scrollView.findViewById(R.id.tables_layout)

        CoroutineScope(Dispatchers.IO).launch {
            val sortTables = MainActivity.subjectTableManager.getAll().sortedBy { it.uid }
            withContext(Dispatchers.Main) {
                for(table in sortTables) {
                    tablesLayout.addView(makeDay(inflater, tablesLayout, table))
                }
            }
        }

        val settingButton: AppCompatButton = binding.findViewById(R.id.setting_button)
        settingButton.setOnClickListener {
            timetableFragment.openTimetableSetting(TimetableFragment.Direction.NEXT)
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

    private fun makeDay(inflater: LayoutInflater, container: ViewGroup, table: SubjectTable): View {
        val view = inflater.inflate(R.layout.timetable_day_layout, container, false)
        view.findViewById<TextView>(R.id.day_name).text = "${weekday[table.uid.ordinal]}요일"
        val subjectLayout: LinearLayout = view.findViewById(R.id.subject_layout)
        for(subject in table.subjects) {
            subjectLayout.addView(
                makeSubject(inflater, subjectLayout, subject.name, subject.teacherName?: "ERROR")
            )
        }
        return view
    }
    private fun makeSubject(inflater: LayoutInflater, container: ViewGroup, subjectName: String, teacherName: String): View {
        return inflater.inflate(R.layout.timetable_subject_layout, container, false).apply {
            findViewById<TextView>(R.id.subject_name).text = subjectName
            findViewById<TextView>(R.id.teacher_name).text = teacherName
        }
    }
}