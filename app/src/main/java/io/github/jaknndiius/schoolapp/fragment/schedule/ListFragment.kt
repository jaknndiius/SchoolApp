package io.github.jaknndiius.schoolapp.fragment.schedule

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import io.github.jaknndiius.schoolapp.dialog.ScheduleInfoDialog
import io.github.jaknndiius.schoolapp.fragment.ScheduleFragment
import io.github.jaknndiius.schoolapp.preset.Direction
import io.github.jaknndiius.schoolapp.preset.RangeType
import io.github.jaknndiius.schoolapp.preset.SortType
import io.github.jaknndiius.schoolapp.view.ListViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ListFragment(
    val scheduleFragment: ScheduleFragment
) : Fragment() {

    private lateinit var binding: View

    lateinit var adapter: ScheduleListAdapter

    companion object {
        var sortType = SortType.ALARM
    }

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

        val listView: ListView = binding.findViewById(R.id.schedule_list)
        adapter = ScheduleListAdapter()

        CoroutineScope(Dispatchers.IO).launch {
            val schedules = MainActivity.scheduleManager.getAll()
            withContext(Dispatchers.Main) {
                schedules.forEach { adapter.addItem(it) }
                when(sortType) {
                    SortType.ALARM -> adapter.sortWithAlarmTime()
                    SortType.GENERATED -> adapter.sortWithGeneratedTime()
                    SortType.TITLE -> adapter.sortWithTitle()
                }
                listView.adapter = adapter
            }
        }

        binding.findViewById<AppCompatButton>(R.id.more_setting_button).setOnClickListener { v ->
            PopupMenu(context, v).run {
                // groupId/itemId/order/title
                menu.add(0, 0, 0, R.string.delete)
                menu.addSubMenu(0, 1, 0, R.string.sort).apply {
                    clearHeader()
                    add(1, 2, 0, R.string.sort_type_alam).apply {
                        isCheckable = true
                        isChecked = sortType == SortType.ALARM
                    }
                    add(1, 3, 0, R.string.sort_type_generated).apply {
                        isCheckable = true
                        isChecked = sortType == SortType.GENERATED
                    }
                    add(1, 4, 0, R.string.sort_type_title).apply {
                        isCheckable = true
                        isChecked = sortType == SortType.TITLE
                    }
                }

                setOnMenuItemClickListener { menuItem ->
                    when(menuItem.itemId) {
                        /* Edit */ 0 -> Toast.makeText(context, R.string.request_long_click_to_delete_schedule, Toast.LENGTH_SHORT).show()
                        /* 알림시간 */ 2 -> adapter.sortWithAlarmTime()
                        /* 생성시각 */ 3 -> adapter.sortWithGeneratedTime()
                        /* 제목 */ 4 -> adapter.sortWithTitle()
                    }
                    false
                }
                show()
            }
        }
        return binding
    }

    inner class ScheduleListAdapter: ListViewAdapter<Schedule>() {

        fun sortWithAlarmTime() {
            sortType = SortType.ALARM
            sortWith(compareBy { it.date })
            notifyDataSetChanged()
        }

        fun sortWithGeneratedTime() {
            sortType = SortType.GENERATED
            sortWith(compareBy { it.id })
            notifyDataSetChanged()
        }

        fun sortWithTitle() {
            sortType = SortType.TITLE
            sortWith(compareBy { it.name})
            notifyDataSetChanged()
        }

        fun deleteSchedule(schedule: Schedule) {
            AlertDialog.Builder(context)
                .setMessage(R.string.ask_really_delete_schedule)
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        MainActivity.scheduleManager.delete(schedule.id)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, R.string.say_deleted_schedule, Toast.LENGTH_LONG).show()
                            removeItem(schedule)
                            notifyDataSetChanged()
                        }
                    }
                }
                .setNegativeButton(getString(R.string.no),  null)
                .show()
        }

        @SuppressLint("ServiceCast")
        @RequiresApi(Build.VERSION_CODES.O)
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return LayoutInflater.from(context).inflate(R.layout.schedule_list_element, parent, false).apply {

                val item = getItem(position)
                findViewById<TextView>(R.id.date).text = item.displayDate //"${item.date.monthValue}월 ${item.date.dayOfMonth}일"
                findViewById<TextView>(R.id.class_number).text = "${item.classNumber}교시"
                findViewById<TextView>(R.id.subject_name).text = item.name
                findViewById<TextView>(R.id.info).text = item.detail

                setOnClickListener {
                    ScheduleInfoDialog(context, this@ListFragment, item).show()
                }

                setOnLongClickListener {
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if(Build.VERSION.SDK_INT >= 26) vibrator.vibrate(VibrationEffect.createOneShot(80, 50))
                    else vibrator.vibrate(80)
                    MainActivity.notificationManager.notify("알림", item.name, item.classNumber)

                    deleteSchedule(item)
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