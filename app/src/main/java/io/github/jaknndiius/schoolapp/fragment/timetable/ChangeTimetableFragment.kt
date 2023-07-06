package io.github.jaknndiius.schoolapp.fragment.timetable

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipDescription
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.*
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.database.WeekDay
import io.github.jaknndiius.schoolapp.enums.Direction
import io.github.jaknndiius.schoolapp.fragment.TimetableFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList

class ChangeTimetableFragment(
    private val timetableFragment: TimetableFragment
) : Fragment() {

    private val SUBJECT_TABLE_VIEW = "SubjectTableView"

    lateinit var weekday: Array<String>

    lateinit var binding: View
    lateinit var inflater: LayoutInflater

    lateinit var subjectLayout: LinearLayout
    lateinit var insertBlank: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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

        subjectLayout = binding.findViewById<LinearLayout>(R.id.subject_layout).apply {
            setOnDragListener{ v, e ->
                when (e.action) {
                    DragEvent.ACTION_DRAG_STARTED -> e.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    DragEvent.ACTION_DRAG_ENTERED -> true
                    DragEvent.ACTION_DRAG_LOCATION -> true
                    DragEvent.ACTION_DRAG_EXITED -> true
                    DragEvent.ACTION_DROP -> true
                    DragEvent.ACTION_DRAG_ENDED -> {
                        if(!e.result) Toast.makeText(context, getString(R.string.request_right_drag), Toast.LENGTH_SHORT).show()
                        this.removeView(insertBlank)
                        true
                    }
                    else -> false
                }
            }
        }
        insertBlank = makeBlank()

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
                        it.addView(makeDraggableSubject(it, subject.name))
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
                getString(R.string.ask_really_init), DialogInterface.OnClickListener { _, _ ->
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
            MainActivity.subjectTableManager.addOrUpdate(weekDay, getSubjectsSet())
            withContext(Dispatchers.Main) {
                reloadButton()
            }
        }
    }

    private fun getSubjectsInTable(): List<View> {
        val outputViews: ArrayList<View> = arrayListOf()
        try {
            binding.findViewsWithText(outputViews, SUBJECT_TABLE_VIEW, LinearLayout.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
        } catch (_: Exception) {}
        return outputViews
    }

    private fun getSubjectsSet(): List<String> {
        return getSubjectsInTable().map { it.findViewById<TextView>(R.id.subject_name).text.toString() }
    }

    private fun reloadDaySubject(weekDay: WeekDay) {

        CoroutineScope(Dispatchers.IO).launch {
            val subjects = MainActivity.subjectTableManager.get(weekDay).subjects

            withContext(Dispatchers.Main) {
                subjectLayout.removeAllViews()
                if(subjects.isEmpty()) subjectLayout.addView(insertBlank)
                else subjects.forEach {
                    subjectLayout.addView(makeTableSubject(it.name))
                }
                reloadButton()
            }
        }
    }

    private fun makeSubject(container: ViewGroup, subjectName: String): View {
        return inflater.inflate(R.layout.timetable_drop_subject_layout, container, false).apply {
            findViewById<TextView>(R.id.subject_name).text = subjectName
        }
    }

    private fun makeDraggableSubject(container: ViewGroup, subjectName: String): View {
        return makeSubject(container, subjectName).apply {
            findViewById<TextView>(R.id.subject_name).text = subjectName
            tag = subjectName
            setOnLongClickListener { v ->
                val item = ClipData.Item(v.tag as? CharSequence)
                val dragData = ClipData(
                    v.tag as? CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item
                )
                val shadow = SubjectDragShadowBuilder(this)
                v.startDragAndDrop(
                    dragData,
                    shadow,
                    null,
                    0
                )
                true
            }
        }
    }

    private fun settingIsChanged(): Boolean {
        val currentSetting = getSubjectsSet()
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

    private fun removeSubject(view: View) {
        subjectLayout.removeView(view)
        reloadButton()
        if(subjectLayout.isEmpty()) subjectLayout.addView(insertBlank)
    }

    private fun makeBlank(): View {
        return makeSubject(subjectLayout, "+").apply {
            setOnDragListener{ v, e ->
                when (e.action) {
                    DragEvent.ACTION_DRAG_STARTED -> e.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    DragEvent.ACTION_DRAG_ENTERED ->  {
                        v.findViewById<LinearLayout>(R.id.subject_background).startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_down))
                        true
                    }
                    DragEvent.ACTION_DRAG_LOCATION -> true
                    DragEvent.ACTION_DROP -> {
                        val item: ClipData.Item = e.clipData.getItemAt(0)
                        val dragData = item.text
                        subjectLayout.addView(
                            makeTableSubject(dragData.toString()),
                            subjectLayout.indexOfChild(v)
                        )
                        reloadButton()
                        subjectLayout.removeView(v)
                        true
                    }
                    DragEvent.ACTION_DRAG_EXITED -> {
                        v.findViewById<LinearLayout>(R.id.subject_background).startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up))
                        true
                    }
                    DragEvent.ACTION_DRAG_ENDED -> {

                        true
                    }
                    else -> false
                }
            }
        }
    }


    private fun makeTableSubject(subjectName: String): View {

        return makeSubject(subjectLayout, subjectName).apply {
            contentDescription = SUBJECT_TABLE_VIEW
            tag = subjectName
            setOnLongClickListener {v ->
                val item = ClipData.Item(v.tag as? CharSequence)
                val dragData = ClipData(
                    v.tag as? CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN, "TEST"),
                    item
                )
                val shadow = SubjectDragShadowBuilder(this)
                v.startDragAndDrop(
                    dragData,
                    shadow,
                    null,
                    0
                )
                removeSubject(v)
                true
            }
            setOnDragListener{ v, e ->
                when (e.action) {
                    DragEvent.ACTION_DRAG_STARTED -> e.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)

                    DragEvent.ACTION_DRAG_ENTERED ->  {
                        v.findViewById<LinearLayout>(R.id.subject_background).startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_down))
                        true
                    }
                    DragEvent.ACTION_DRAG_LOCATION -> {
                        subjectLayout.removeView(insertBlank)
                        val currentIdx = subjectLayout.indexOfChild(v)
                        subjectLayout.addView(insertBlank,
                            if(e.y <= v.height /2) currentIdx else currentIdx+1)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            binding.findViewById<ScrollView>(R.id.scroll_subjects).scrollToDescendant(insertBlank)
                        }
                        true
                    }
                    DragEvent.ACTION_DRAG_EXITED -> {
                        v.findViewById<LinearLayout>(R.id.subject_background).startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up))
                        true
                    }
                    DragEvent.ACTION_DROP -> {
                        subjectLayout.removeView(insertBlank)
                        val item: ClipData.Item = e.clipData.getItemAt(0)
                        val dragData = item.text
                        subjectLayout.addView(
                            makeTableSubject(dragData.toString()),
                            subjectLayout.indexOfChild(v)
                        )
                        reloadButton()
                        subjectLayout.removeView(v)
                        true
                    }
                    DragEvent.ACTION_DRAG_ENDED -> true
                    else -> false
                }
            }
        }
    }
    private class SubjectDragShadowBuilder(view: View): View.DragShadowBuilder(view) {

        private val shadow = ColorDrawable(Color.LTGRAY)

        override fun onProvideShadowMetrics(size: Point, touch: Point) {

            val width: Int = view.width

            val height: Int = view.height

            shadow.setBounds(0, 0, width, height)

            size.set(width, height)

            touch.set(width / 2, height / 2)
        }

        override fun onDrawShadow(canvas: Canvas) {
            shadow.draw(canvas)
        }
    }
}