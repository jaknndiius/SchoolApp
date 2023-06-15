package io.github.jaknndiius.schoolapp.fragment.timetable

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.get
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.database.WeekDay
import io.github.jaknndiius.schoolapp.fragment.TimetableFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ChangeTimetableFragment(
    private val timetableFragment: TimetableFragment
) : Fragment() {

    lateinit var inflater: LayoutInflater

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        this.inflater = inflater

        val binding = inflater.inflate(R.layout.timetable_setting_change_timetable, container, false)

        binding.findViewById<AppCompatButton>(R.id.back_button).setOnClickListener {
            timetableFragment.openTimetableSetting(TimetableFragment.Direction.PREVIOUS)
        }

        val subjectLayout: LinearLayout = binding.findViewById(R.id.subject_layout)

        val radioGroup: RadioGroup = binding.findViewById(R.id.day_radio_group)
        radioGroup.setOnCheckedChangeListener { radioGroup, id ->
            reloadDaySubject(
                when(id) {
                    R.id.monday -> WeekDay.MONDAY
                    R.id.tuesday -> WeekDay.TUESDAY
                    R.id.wednesday -> WeekDay.WENDESDAY
                    R.id.thursday -> WeekDay.THURSDAY
                    R.id.friday -> WeekDay.FRIDAY
                    else -> WeekDay.MONDAY
                },
                subjectLayout
            )
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

        return binding
    }

    private fun reloadDaySubject(weekDay: WeekDay, container: ViewGroup) {
        container.removeAllViews()
        CoroutineScope(Dispatchers.IO).launch {
            val todaySubjects = MainActivity.subjectTableManager.get(weekDay).subjects
            withContext(Dispatchers.Main) {
                todaySubjects.forEach {
                    container.addView(makeTableSubject(container, it.name))
                }
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

    private fun removeSubject(view: View, container: ViewGroup) {
        container.removeView(view)
    }

    private fun makeTableSubject(container: ViewGroup, subjectName: String): View {

        fun setMarginBottom(view: View, margin: Int) {
            val par = view.layoutParams as LayoutParams
            par.bottomMargin = margin
            view.layoutParams = par
        }

        return makeSubject(container, subjectName).apply {
            setOnLongClickListener {
                removeSubject(this, container)
                true
            }
            setOnDragListener{ v, e ->
                when (e.action) {
                    DragEvent.ACTION_DRAG_STARTED -> e.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    DragEvent.ACTION_DRAG_ENTERED ->  {
                        setMarginBottom(this, 90)
                        true
                    }
                    DragEvent.ACTION_DRAG_LOCATION -> {
                        true
                    }
                    DragEvent.ACTION_DRAG_EXITED -> {
                        setMarginBottom(this, 30)
                        true
                    }
                    DragEvent.ACTION_DROP -> {

                        setMarginBottom(this, 30)

                        val item: ClipData.Item = e.clipData.getItemAt(0)
                        val dragData = item.text

                        container.addView(
                            makeTableSubject(container, dragData.toString()),
                            container.indexOfChild(this) +1
                        )
                        true
                    }
                    DragEvent.ACTION_DRAG_ENDED -> {
                        if(!e.result) Toast.makeText(context, "시간표 안에 과목을 드래그 해주세요.", Toast.LENGTH_LONG).show()
                        true
                    }
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