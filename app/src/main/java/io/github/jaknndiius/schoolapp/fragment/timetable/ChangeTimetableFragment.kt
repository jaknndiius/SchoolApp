package io.github.jaknndiius.schoolapp.fragment.timetable

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.database.WeekDay
import io.github.jaknndiius.schoolapp.preset.Direction
import io.github.jaknndiius.schoolapp.fragment.TimetableFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangeTimetableFragment(
    private val timetableFragment: TimetableFragment
) : Fragment() {

    lateinit var weekday: Array<String>

    lateinit var binding: View
    lateinit var inflater: LayoutInflater

    lateinit var draggableObject: DraggableObject

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        weekday = resources.getStringArray(R.array.weekday_names)

        this.inflater = inflater

        binding = inflater.inflate(R.layout.timetable_setting_change_timetable, container, false)

        binding.findViewById<AppCompatButton>(R.id.back_button).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val isChanged = settingIsChanged()
                withContext(Dispatchers.Main) {
                   if(isChanged) askAndRun(getString(R.string.ask_really_leave_without_save), DialogInterface.OnClickListener { _, _ ->
                           timetableFragment.openTimetableSetting(Direction.PREVIOUS_VERTICAL)
                       })
                   else timetableFragment.openTimetableSetting(Direction.PREVIOUS_VERTICAL)
                }
            }
        }

        draggableObject = DraggableObject(context, inflater, binding, binding.findViewById(R.id.subject_layout))
        draggableObject.setOnReloadMethod {
            reloadButton()
        }

        val radioGroup: RadioGroup = binding.findViewById(R.id.day_radio_group)
        radioGroup.setOnCheckedChangeListener { _, id ->
            reloadDaySubject(getWeekdayWithId(id))
        }
        binding.findViewById<RadioButton>(R.id.monday).isChecked = true

        val dropSubjectLayouts: List<LinearLayout> = listOf(
            binding.findViewById(R.id.drop_subject_layout_line1),
            binding.findViewById(R.id.drop_subject_layout_line2),
            binding.findViewById(R.id.drop_subject_layout_line3)
        )
        CoroutineScope(Dispatchers.IO).launch {
            val subjects = MainActivity.subjectManager.getAll()
            withContext(Dispatchers.Main) {
                subjects.forEachIndexed { index, subject ->
                    dropSubjectLayouts[index%3].let {
                        it.addView(draggableObject.makeDraggableSubject(it, subject.name))
                    }
                }
            }
        }
        reloadDaySubject(WeekDay.MONDAY)

        binding.findViewById<AppCompatButton>(R.id.save_button).setOnClickListener {
            val currentWeekDay = getCheckedWeekday()
            val currentKoreanWeekDay =  weekday[currentWeekDay.ordinal]
            askAndRun(
                getString(R.string.ask_save_at_weekday, currentKoreanWeekDay), DialogInterface.OnClickListener { _, _ ->
                    save(currentWeekDay)
                    Toast.makeText(context, getString(R.string.say_saved_subjects_with_weekday, currentKoreanWeekDay), Toast.LENGTH_LONG).show()
                })
        }

        binding.findViewById<AppCompatButton>(R.id.init_button).setOnClickListener {
            askAndRun(
                getString(R.string.ask_really_init_day_timetable), DialogInterface.OnClickListener { _, _ ->
                    reloadDaySubject(getCheckedWeekday())
                    Toast.makeText(context, getString(R.string.say_initialized_at_weekday), Toast.LENGTH_LONG).show()
                })
        }
        return binding
    }

    private fun askAndRun(message: String, onYesListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton(getString(R.string.yes), onYesListener)
            .setNegativeButton(getString(R.string.no),  null)
            .show()
    }

    private fun getWeekdayWithId(id: Int): WeekDay {
        return when(id) {
            R.id.monday -> WeekDay.MONDAY
            R.id.tuesday -> WeekDay.TUESDAY
            R.id.wednesday -> WeekDay.WENDESDAY
            R.id.thursday -> WeekDay.THURSDAY
            R.id.friday -> WeekDay.FRIDAY
            else -> WeekDay.MONDAY
        }
    }
    private fun getCheckedWeekday(): WeekDay {
        return getWeekdayWithId(binding.findViewById<RadioGroup>(R.id.day_radio_group).checkedRadioButtonId)
    }

    private fun save(weekDay: WeekDay) {
        CoroutineScope(Dispatchers.IO).launch {
            MainActivity.subjectTableManager.addOrUpdate(weekDay, draggableObject.getSettings())
            withContext(Dispatchers.Main) {
                reloadButton()
            }
        }
    }

    private fun reloadDaySubject(weekDay: WeekDay) {

        CoroutineScope(Dispatchers.IO).launch {
            val subjects = MainActivity.subjectTableManager.get(weekDay).subjects
            withContext(Dispatchers.Main) {
                draggableObject.reloadTableWithObjects(subjects.map { it.name })
            }
        }
    }

    private fun settingIsChanged(): Boolean {
        val currentSetting = draggableObject.getSettings()
        val databaseSetting = MainActivity.subjectTableManager.get(getCheckedWeekday()).subjects.map { it.name }
        return currentSetting != databaseSetting
    }

    private fun reloadButton() {
        CoroutineScope(Dispatchers.IO).launch {
            val isChanged = settingIsChanged()
            withContext(Dispatchers.Main) {
                binding.findViewById<AppCompatButton>(R.id.save_button).isEnabled = isChanged
                binding.findViewById<AppCompatButton>(R.id.init_button).isEnabled = isChanged
            }
        }
    }
}