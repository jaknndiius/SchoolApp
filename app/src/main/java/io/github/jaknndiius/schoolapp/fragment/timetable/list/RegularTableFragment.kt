package io.github.jaknndiius.schoolapp.fragment.timetable.list

import android.graphics.Rect
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TableLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.animation.CustomAnimation
import io.github.jaknndiius.schoolapp.database.Subject
import io.github.jaknndiius.schoolapp.database.SubjectTable
import io.github.jaknndiius.schoolapp.database.WeekDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.abs

class RegularTableFragment: Fragment() {

    private val weekday = listOf("월", "화", "수", "목", "금")
    private lateinit var binding: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = inflater.inflate(R.layout.timetable_list_table, container, false)

        val tableLayout = binding.findViewById<LinearLayout>(R.id.tables_layout)
        tableLayout.visibility = View.INVISIBLE

        CoroutineScope(Dispatchers.IO).launch {

            val sortTables = MainActivity.subjectTableManager.getAll().sortedBy { it.uid }

            withContext(Dispatchers.Main) {
                for(table in sortTables) {
                    tableLayout.addView(makeDay(inflater, tableLayout, table))
                }
                Handler().postDelayed({
                    scrollToCurrentDay(binding.findViewById(R.id.tables_scroll), tableLayout)
                    tableLayout.visibility = View.VISIBLE
                }, 10)
            }
        }

        return binding
    }

    private fun getDay(): Int {
        return Date().day.coerceAtLeast(1).coerceAtMost(5)
    }

    private fun scrollToCurrentDay(scrollView: HorizontalScrollView, tableLayout: LinearLayout) {
        val currentDayView: View = tableLayout.getChildAt(getDay() -1)
        scrollView.scrollX = currentDayView.x.toInt() - 100
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