package io.github.jaknndiius.schoolapp.fragment.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.database.Subject
import io.github.jaknndiius.schoolapp.preset.Direction
import io.github.jaknndiius.schoolapp.fragment.TimetableFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExamSetterFragment(
    private val timetableFragment: TimetableFragment,
) : Fragment() {

    private lateinit var binding: View
    private lateinit var inflater: LayoutInflater

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = inflater.inflate(R.layout.timetable_setting_set_exam, container, false)
        this.inflater = inflater

        binding.findViewById<Button>(R.id.back_button).setOnClickListener {
            timetableFragment.openTimetableSetting(Direction.PREVIOUS_VERTICAL)
        }

        reloadSubjects()

        return binding
    }

    private fun reloadSubjects() {

        val subjectLayouts: List<LinearLayout> = listOf(
            binding.findViewById(R.id.subject_layout_line1),
            binding.findViewById(R.id.subject_layout_line2),
            binding.findViewById(R.id.subject_layout_line3)
        )

        CoroutineScope(Dispatchers.IO).launch {
            val subjects = MainActivity.subjectManager.getAll()
            withContext(Dispatchers.Main) {
                subjects.forEachIndexed { index, subject ->
                    subjectLayouts[index%3].let {
                        it.addView(makeSubjectView(it, subject))
                    }
                }
            }

        }
    }

    private fun makeSubjectView(container: ViewGroup, subject: Subject): View {
        return inflater.inflate(R.layout.fragment_set_exam, container, false).apply {

            findViewById<TextView>(R.id.subject_name).apply {
                text = subject.name
                setTextColor(
                    resources.getColor(
                        if(subject.examAttr == null) R.color.gray
                        else R.color.green
                    )
                )
            }
            findViewById<TextView>(R.id.teacher_name).apply {
                text = subject.teacherName?: "-"
                setTextColor(
                    resources.getColor(
                        if(subject.examAttr == null) R.color.gray
                        else R.color.green
                    )
                )
            }
            findViewById<LinearLayout>(R.id.table_row).background = resources.getDrawable(
                if(subject.examAttr == null) R.drawable.bg_white_c4_b1gray
                else R.drawable.bg_white_c4_b1green
            )

            setOnClickListener {
                timetableFragment.openExamUpdater(Direction.NEXT_VERTICAL, subject)
            }
        }
    }
}