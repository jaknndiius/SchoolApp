package io.github.jaknndiius.schoolapp.fragment.timetable

import android.app.AlertDialog
import android.content.DialogInterface
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.database.ExamAttr
import io.github.jaknndiius.schoolapp.database.Subject
import io.github.jaknndiius.schoolapp.preset.Direction
import io.github.jaknndiius.schoolapp.fragment.TimetableFragment
import io.github.jaknndiius.schoolapp.preset.RangeType
import io.github.jaknndiius.schoolapp.view.ListViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ExamUpdaterFragment(
    private val timetableFragment: TimetableFragment,
    private val subject: Subject
) : Fragment() {

    private val RANGEINFO_LAYOUT = "rangeinfoLayout"

    lateinit var binding: View

    lateinit var adapter: ExamListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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

            val currentRanges = getRangeInfos()
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

        subject.examAttr?.questionsCount?.let { questions ->
            binding.findViewById<TextView>(R.id.question_count0).text = questions[0].toString()
            binding.findViewById<TextView>(R.id.question_count1).text = questions[1].toString()
        }

        adapter = loadListView()

        binding.findViewById<Button>(R.id.add_exam_button).setOnClickListener {
            adapter.addItem(RangeType.TEXTBOOK to "")
            adapter.notifyDataSetChanged()
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

    private fun getRangeInfos() = adapter.getList()

    private fun save(afterRun: () -> Unit) {
        val question0 = binding.findViewById<TextView>(R.id.question_count0).text.toString()
        val question1 = binding.findViewById<TextView>(R.id.question_count1).text.toString()
        val ranges = getRangeInfos()

        val examAttr = ExamAttr(
            if(question0.isBlank() && question1.isBlank()) null else listOf(
                if(question0.isBlank()) 0 else question0.toInt(),
                if(question1.isBlank()) 0 else question1.toInt()
            )
        ).apply {
            this.ranges = getRangeInfos()
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

    private fun loadListView(): ExamListAdapter {
        val listView: ListView = binding.findViewById(R.id.range_list)
        val adapter = ExamListAdapter()

        subject.examAttr?.let {
            it.ranges.forEach { rangeInfo ->
                adapter.addItem(rangeInfo)
            }
        }
        listView.adapter = adapter
        return adapter
    }

    inner class ExamListAdapter: ListViewAdapter<Pair<RangeType, String>>() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return LayoutInflater.from(context).inflate(R.layout.rangeinfo_layout, parent, false).apply {
                contentDescription = RANGEINFO_LAYOUT

                val item = getItem(position)

                findViewById<AppCompatButton?>(R.id.range_type_button).apply {
                    setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(item.first.drawableId), null, null)
                    text = item.first.korean
                    setOnClickListener { v ->
                        PopupMenu(context, v).run {
                            RangeType.values().forEach { rangeType ->
                                menu.add(rangeType.korean)
                            }
                            setOnMenuItemClickListener { menuItem ->
                                val rangeType = RangeType.values().first { it.korean == menuItem.title.toString() }
                                setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(rangeType.drawableId), null, null)
                                text = rangeType.korean
                                modifyItem(position, rangeType to getItem(position).second)
                                false
                            }
                            show()
                        }
                    }
                }

                findViewById<EditText?>(R.id.input_range_info).apply {
                    setText(item.second)
                    addTextChangedListener(object : TextWatcher {
                        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }

                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }

                        override fun afterTextChanged(s: Editable) {
                            modifyItem(position, getItem(position).first to s.toString())
                        }
                    })
                }

                findViewById<ImageButton>(R.id.delete_line_button).setOnClickListener {
                    removeItem(position)
                    this@ExamListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

}