package io.github.jaknndiius.schoolapp.fragment.timetable.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.database.ExamTable
import io.github.jaknndiius.schoolapp.database.Subject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExamTableFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = inflater.inflate(R.layout.timetable_list_table, container, false)

        val tableLayout: LinearLayout = binding.findViewById(R.id.tables_layout)

        CoroutineScope(Dispatchers.IO).launch {
            val examTables = MainActivity.examTableManager.getAll()

            examTables.maxOfOrNull { it.subjects.size }?.let { max ->
                examTables.forEach {
                    if(it.subjects.size == max) return@forEach

                    val newlist = ArrayList(it.subjects)
                    while (newlist.size != max) {
                        newlist.add(Subject("", ""))
                    }
                    it.subjects = newlist
                }
            }

            withContext(Dispatchers.Main) {
                tableLayout.removeAllViews()
                for(table in examTables) {
                    tableLayout.addView(makeExam(inflater, tableLayout, table))
                }
                binding.findViewById<HorizontalScrollView>(R.id.tables_scroll).scrollX = 0
            }
        }

        return binding
    }

    private fun makeExam(inflater: LayoutInflater, container: ViewGroup, table: ExamTable): View {
        val view = inflater.inflate(R.layout.timetable_day_layout, container, false)
        view.findViewById<TextView>(R.id.day_name).text = table.name
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