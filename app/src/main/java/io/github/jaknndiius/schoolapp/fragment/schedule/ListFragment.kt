package io.github.jaknndiius.schoolapp.fragment.schedule

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.database.Schedule
import io.github.jaknndiius.schoolapp.fragment.ScheduleFragment
import io.github.jaknndiius.schoolapp.preset.Direction
import io.github.jaknndiius.schoolapp.preset.RangeType
import io.github.jaknndiius.schoolapp.view.ListViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ListFragment(
    private val scheduleFragment: ScheduleFragment
) : Fragment() {

    private lateinit var binding: View

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = inflater.inflate(R.layout.schedule_list, container, false)

        binding.findViewById<AppCompatButton>(R.id.add_schedule_button).setOnClickListener {
            scheduleFragment.openScheduleGenerator(Direction.NEXT_VERTICAL)
        }
        binding.findViewById<AppCompatButton>(R.id.more_setting_button).setOnClickListener {v ->
            PopupMenu(context, v).run {
                // groupId/itemId/order/title
                menu.add(0, 0, 0, "편집")
                menu.addSubMenu(0, 1, 0, "정렬").apply {
                    clearHeader()
                    add(1, 2, 0, "알림 시간 순서")
                    add(1, 3, 0, "생성 시각 순서")
                    add(1, 4, 0, "직접 설정한 순서")
                }
                menu.add(0, 5, 0, "설정")

                setOnMenuItemClickListener { menuItem ->
                    when(menuItem.itemId) {
                        /* Edit */ 0 -> {}
                        /* 알림시간 */ 2 -> {}
                        /* 생성시각 */ 3 -> {}
                        /* 직접설정 */ 4 -> {}
                        /* 설정 */ 5 -> {}
                    }
                    false
                }
                show()
            }
        }

        fill()
        return binding
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fill() {
        val listView: ListView = binding.findViewById(R.id.schedule_list)
        val adapter = ScheduleListAdapter()

        CoroutineScope(Dispatchers.IO).launch {
            val schedules = MainActivity.scheduleManager.getAll()
            withContext(Dispatchers.Main) {
                schedules.forEach { adapter.addItem(it) }
                listView.adapter = adapter
            }
        }
    }

    inner class ScheduleListAdapter: ListViewAdapter<Schedule>() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return LayoutInflater.from(context).inflate(R.layout.schedule_list_element, parent, false).apply {

                val item = getItem(position)
                findViewById<TextView>(R.id.date).text = item.displayDate //"${item.date.monthValue}월 ${item.date.dayOfMonth}일"
                findViewById<TextView>(R.id.class_number).text = "${item.classNumber}교시"
                val titleView = findViewById<TextView>(R.id.subject_name).apply { text = item.name }
                findViewById<TextView>(R.id.info).text = item.detail

                setOnLongClickListener {
                    AlertDialog.Builder(context)
                        .setMessage("일정을 삭제하시겠습니까?")
                        .setPositiveButton(getString(R.string.yes)) { _, _ ->
                            CoroutineScope(Dispatchers.IO).launch {
                                MainActivity.scheduleManager.delete(titleView.text.toString())
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "일정을 삭제했습니다.", Toast.LENGTH_LONG).show()
                                    removeItem(position)
                                    notifyDataSetChanged()
                                }
                            }
                        }
                        .setNegativeButton(getString(R.string.no),  null)
                        .show()
                    false
                }

                findViewById<CheckBox>(R.id.alarm_checkbox).setOnCheckedChangeListener { _, checked ->
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