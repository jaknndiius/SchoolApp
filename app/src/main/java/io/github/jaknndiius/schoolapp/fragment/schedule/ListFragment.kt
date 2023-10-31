package io.github.jaknndiius.schoolapp.fragment.schedule

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.fragment.ScheduleFragment
import io.github.jaknndiius.schoolapp.view.ListViewAdapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ListFragment(
    val scheduleFragment: ScheduleFragment
) : Fragment() {

    private lateinit var binding: View

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflater.inflate(R.layout.schedule_list, container, false)

        fill()
        return binding
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fill() {
        val listView: ListView = binding.findViewById(R.id.schedule_list)
        val adapter = ScheduleListAdapter()
        adapter.addItem(Schedule(LocalDateTime.now(), 8, "지구과학", "한반도의 지질구조요?"))
        adapter.addItem(Schedule(LocalDateTime.now(), 4, "물리", "skeh ahffk"))
        adapter.addItem(Schedule(LocalDateTime.now(), 4, "물리", "skeh ahffk"))
        adapter.addItem(Schedule(LocalDateTime.now(), 434, "물리", "skeh ahffk"))
        adapter.addItem(Schedule(LocalDateTime.now(), 4, "물리", "skeh ahffk"))
        adapter.addItem(Schedule(LocalDateTime.now(), 4, "물리", "skeh ahffk"))
        adapter.addItem(Schedule(LocalDateTime.now(), 4, "물리", "skeh ahffk"))
        adapter.addItem(Schedule(LocalDateTime.now(), 400, "물리", "skeh ahffk"))
        adapter.addItem(Schedule(LocalDateTime.now(), 4, "물리", "skeh ahffk"))

        listView.adapter = adapter
    }

    data class Schedule(
        val date: LocalDateTime,
        val classNumber: Int,
        val subjectName: String,
        val info: String
    )

    inner class ScheduleListAdapter: ListViewAdapter<Schedule>() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return LayoutInflater.from(context).inflate(R.layout.schedule_list_element, parent, false).apply {
                val item = getItem(position)
                findViewById<TextView>(R.id.date).text = "${item.date.monthValue}월 ${item.date.dayOfMonth}일"
                findViewById<TextView>(R.id.class_number).text = "${item.classNumber}교시"
                findViewById<TextView>(R.id.subject_name).text = item.subjectName
                findViewById<TextView>(R.id.info).text = item.info

                findViewById<CheckBox>(R.id.alarm_checkbox).setOnCheckedChangeListener { compoundButton, checked ->
                    if(checked) {
                        findViewById<LinearLayout>(R.id.schedule_background).setBackgroundResource(R.drawable.bg_white_c10)
                    } else {
                        findViewById<LinearLayout>(R.id.schedule_background).setBackgroundResource(R.drawable.bg_lightgray_c10)

                    }
                }
            }
        }
    }
}