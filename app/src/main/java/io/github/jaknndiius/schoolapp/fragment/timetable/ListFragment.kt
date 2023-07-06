package io.github.jaknndiius.schoolapp.fragment.timetable

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import io.github.jaknndiius.schoolapp.enums.Direction
import io.github.jaknndiius.schoolapp.fragment.TimetableFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ListFragment(
    private val timetableFragment: TimetableFragment
) : Fragment() {

    private lateinit var weekday: Array<String>
    private lateinit var binding: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        weekday = resources.getStringArray(R.array.weekday_names)
        binding = inflater.inflate(R.layout.timetable_list, container, false)
        val scrollView: HorizontalScrollView = binding.findViewById(R.id.tables_scroll)
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
            timetableFragment.openTimetableSetting(Direction.NEXT)
        }

        Handler().postDelayed({reload()}, 100)
        return binding
    }

    private fun getDay(): Int {
        // Sunday:0 ~ Saturday: 6
        return Date().day.coerceAtMost(5).coerceAtLeast(1)
    }
    fun getScrollX(): Int {
        return 700*(getDay() -1)
    }

    fun reload() {
        try {
            binding.findViewById<HorizontalScrollView>(R.id.tables_scroll).scrollX = getScrollX()
        }catch (e: Exception) {Log.d("EEEEEEE", e.message.toString()) }
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