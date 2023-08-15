package io.github.jaknndiius.schoolapp.fragment.timetable

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.database.ExamAttr
import io.github.jaknndiius.schoolapp.database.Subject
import io.github.jaknndiius.schoolapp.enum.Direction
import io.github.jaknndiius.schoolapp.fragment.TimetableFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExamUpdaterFragment(
    private val timetableFragment: TimetableFragment,
    private val subject: Subject
) : Fragment() {

    private val RANGEINFO_LAYOUT = "rangeinfoLayout"

    lateinit var binding: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = inflater.inflate(R.layout.timetable_setting_exam_updater, container, false)

        binding.findViewById<Button>(R.id.back_button).setOnClickListener {

            val ques0 = binding.findViewById<TextView>(R.id.question_count0).text.toString()
            val ques1 = binding.findViewById<TextView>(R.id.question_count1).text.toString()

            val currentQuestions =
                if(ques0.isBlank() && ques1.isBlank()) null
                else listOf(
                    ques0, ques1
                ).map {
                    if(it.isBlank()) 0
                    else it.toInt()
                }

            val currentRanges = getRangeInfos().map { it.findViewById<TextView>(R.id.input_rangeinfo).text.toString() }.filter { it.isNotBlank() }
            val examAttr = subject.examAttr
            if((examAttr == null && (ques0.isNotBlank() || ques1.isNotBlank() || currentRanges.isNotEmpty()))
                    || (examAttr != null && (currentQuestions != examAttr.questionsCount || currentRanges != examAttr.ranges))) {
                AlertDialog.Builder(context)
                    .setMessage(resources.getString(R.string.ask_really_leave_without_save))
                    .setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { _, _ ->
                        timetableFragment.openExamSetter(Direction.PREVIOUS_VERTICAL)
                    })
                    .setNegativeButton(getString(R.string.no),  null)
                    .show()
            } else {
                timetableFragment.openExamSetter(Direction.PREVIOUS_VERTICAL)
            }
        }

        binding.findViewById<TextView>(R.id.title).text = getString(R.string.setting_set_exam_with_subject, subject.name)

        val rangeInfosLayout: LinearLayout = binding.findViewById(R.id.rangeinfos_layout)

        binding.findViewById<Button>(R.id.add_exam_button).setOnClickListener {
            rangeInfosLayout.addView(
                makeRangeInfoLayout(inflater, rangeInfosLayout)
            )
        }

        subject.examAttr?.run {
            questionsCount?.let {questions ->
                binding.findViewById<TextView>(R.id.question_count0).text = questions[0].toString()
                binding.findViewById<TextView>(R.id.question_count1).text = questions[1].toString()
            }
            ranges.forEach { range ->
                rangeInfosLayout.addView(
                    makeRangeInfoLayout(inflater, rangeInfosLayout, range)
                )
            }
        }

        binding.findViewById<Button>(R.id.attach_exam_button).setOnClickListener {
            save {
                Toast.makeText(context, "해당 과목의 시험 정보를 설정했습니다.", Toast.LENGTH_SHORT).show()
                timetableFragment.openExamSetter(Direction.PREVIOUS_VERTICAL)
            }
        }
        binding.findViewById<Button>(R.id.detach_exam_button).setOnClickListener {
            detach {
                Toast.makeText(context, "해당 과목의 시험 정보를 제거했습니다.", Toast.LENGTH_SHORT).show()
                timetableFragment.openExamSetter(Direction.PREVIOUS_VERTICAL)
            }
        }
        return binding
    }

    private fun makeRangeInfoLayout(inflater: LayoutInflater, container: ViewGroup, info: String? = null): View {
        return inflater.inflate(R.layout.rangeinfo_layout, container, false).apply {
            contentDescription = RANGEINFO_LAYOUT
            findViewById<ImageButton>(R.id.delete_line_button).setOnClickListener {
                container.removeView(this)
            }
            if(info != null) findViewById<TextView>(R.id.input_rangeinfo).text = info
        }
    }

    private fun getRangeInfos(): List<View> {
        val outputViews: ArrayList<View> = arrayListOf()
        try {
            binding.findViewsWithText(outputViews, RANGEINFO_LAYOUT, LinearLayout.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
        } catch (_: Exception) {}
        return outputViews
    }

    private fun save(afterRun: () -> Unit) {
        val question0 = binding.findViewById<TextView>(R.id.question_count0).text.toString()
        val question1 = binding.findViewById<TextView>(R.id.question_count1).text.toString()
        val ranges = getRangeInfos().map { it.findViewById<TextView>(R.id.input_rangeinfo).text.toString() }

        val examAttr = ExamAttr(
            if(question0.isBlank() && question1.isBlank()) null else listOf(
                if(question0.isBlank()) 0 else question0.toInt(),
                if(question1.isBlank()) 0 else question1.toInt()
            )
        ).apply {
            this.ranges = ranges
        }

        CoroutineScope(Dispatchers.IO).launch {
            if(question0.isNotBlank() || question1.isNotBlank() || ranges.isNotEmpty())
                MainActivity.subjectManager.attachExam(subject.name, examAttr)
            else
                MainActivity.subjectManager.detachExam(subject.name)

            withContext(Dispatchers.Main) {
                afterRun()
            }
        }
    }

    private fun detach(afterRun: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            MainActivity.subjectManager.detachExam(subject.name)

            withContext(Dispatchers.Main) {
                afterRun()
            }
        }
    }

}