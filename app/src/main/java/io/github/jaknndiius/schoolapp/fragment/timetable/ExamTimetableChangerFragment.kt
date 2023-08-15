package io.github.jaknndiius.schoolapp.fragment.timetable

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.enum.AskResult
import io.github.jaknndiius.schoolapp.enum.Direction
import io.github.jaknndiius.schoolapp.fragment.TimetableFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExamTimetableChangerFragment(
    private val timetableFragment: TimetableFragment
) : Fragment() {

    lateinit var binding: View

    lateinit var draggableObject: DraggableObject

    private var dayId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = inflater.inflate(R.layout.timetable_setting_set_exam_timetable, container, false)

        binding.findViewById<Button>(R.id.back_button).setOnClickListener {
            timetableFragment.openTimetableSetting(Direction.PREVIOUS_VERTICAL)
        }

        draggableObject = DraggableObject(context, inflater, binding, binding.findViewById(R.id.subject_layout))
        draggableObject.setOnReloadMethod {
            reloadButton()
        }

        val radioGroup: RadioGroup = binding.findViewById(R.id.day_radio_group)
        radioGroup.setOnCheckedChangeListener { _, _ ->
            reloadDayExam(getCheckedExam())
        }

        binding.findViewById<Button>(R.id.add_day_button).setOnClickListener {
            getDate { test ->
                when(test.result) {
                    AskResult.SUCCESS -> {
                        val view = makeRadioButton(inflater, radioGroup, test.value)
                        radioGroup.addView(view)
                        view.isChecked = true
                        CoroutineScope(Dispatchers.IO).launch {
                            MainActivity.examTableManager.addOrUpdate(test.value, listOf())
                        }
                        Toast.makeText(context, getString(R.string.say_added_exam), Toast.LENGTH_LONG).show()
                    }
                    AskResult.FALL_ALREADY_EXIST -> {
                        Toast.makeText(context, getString(R.string.say_already_existing_exam), Toast.LENGTH_LONG).show()
                    }
                    AskResult.FALL_NO_INPUT -> {
                        Toast.makeText(context, getString(R.string.request_input_day_or_displayname), Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }

        val dropSubjectLayouts: List<LinearLayout> = listOf(
            binding.findViewById(R.id.drop_subject_layout_line1),
            binding.findViewById(R.id.drop_subject_layout_line2),
            binding.findViewById(R.id.drop_subject_layout_line3)
        )
        CoroutineScope(Dispatchers.IO).launch {

            val examTables = MainActivity.examTableManager.getAll()

            val subjects = MainActivity.subjectManager.getAll()
            withContext(Dispatchers.Main) {
                examTables.forEach {
                    radioGroup.addView(
                        makeRadioButton(inflater, radioGroup, it.name)
                    )
                }
                if(radioGroup.isNotEmpty()) radioGroup.check(radioGroup[0].id)

                subjects.forEachIndexed { index, subject ->
                    dropSubjectLayouts[index%3].let {
                        it.addView(draggableObject.makeDraggableSubject(it, subject.name))
                    }
                }
            }
        }

        binding.findViewById<AppCompatButton>(R.id.save_button).setOnClickListener {
            askAndRun(
                getString(R.string.ask_save_current_exam), DialogInterface.OnClickListener { _, _ ->
                    getCheckedExam()?.let { examName -> save(examName) }
                })
        }

        binding.findViewById<AppCompatButton>(R.id.delete_button).setOnClickListener {
            askAndRun(
                getString(R.string.ask_really_delete_exam), DialogInterface.OnClickListener { _, _ ->
                    getCheckedExam()?.let { name -> delete(name) }
                    radioGroup.removeView(getCheckedExamView())
                    if(radioGroup.isNotEmpty()) {
                        radioGroup.check(radioGroup[0].id)
                        getCheckedExam()?.let { it1 -> reloadDayExam(it1) }
                        reloadButton()
                    } else {
                        draggableObject.clearTable()
                    }

                    Toast.makeText(context, getString(R.string.say_initialized_at_weekday), Toast.LENGTH_LONG).show()
                })
        }

        binding.findViewById<AppCompatButton>(R.id.init_button).setOnClickListener {
            askAndRun(
                getString(R.string.ask_really_init_exam_timetable), DialogInterface.OnClickListener { _, _ ->
                    getCheckedExam()?.let { it1 -> reloadDayExam(it1) }
                    Toast.makeText(context, getString(R.string.say_initialized_at_weekday), Toast.LENGTH_LONG).show()
                })
        }

        return binding
    }

    private fun makeRadioButton(inflater: LayoutInflater, radioGroup: RadioGroup, text: String): RadioButton {
        return (inflater.inflate(R.layout.timetable_day_button, radioGroup, false) as RadioButton).apply {
            this.text = text
            id = ++dayId
        }
    }

    private fun askAndRun(message: String, onYesListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton(getString(R.string.yes), onYesListener)
            .setNegativeButton(getString(R.string.no),  null)
            .show()
    }

    data class Test(
        val result: AskResult,
        val value: String = ""
    )

    private fun getDate(then: (result: Test) -> Unit) {

        val txtEdit = EditText(context)

        AlertDialog.Builder(context)
            .setMessage(getString(R.string.request_input_day_or_displayname))
            .setView(txtEdit)
            .setPositiveButton(getString(R.string.add), DialogInterface.OnClickListener { _, _ ->

                val str = txtEdit.text.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    val isExist = MainActivity.examTableManager.isExist(str)
                    withContext(Dispatchers.Main) {
                        then(
                            if (str.isBlank()) Test(AskResult.FALL_NO_INPUT)
                            else if(isExist) Test(AskResult.FALL_ALREADY_EXIST)
                            else Test(AskResult.SUCCESS, str)
                        )
                    }
                }
            })
            .setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener { _, _ ->
                then(Test(AskResult.FALL_USER_CANCEL))
            })
            .show()
    }

    private fun getCheckedExamView(): RadioButton? {
        val radioGroup: RadioGroup = binding.findViewById(R.id.day_radio_group)
        return radioGroup.findViewById(radioGroup.checkedRadioButtonId)
    }

    private fun getCheckedExam(): String {
        return getCheckedExamView()?.text.toString()
    }

    private fun save(examName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            MainActivity.examTableManager.addOrUpdate(examName, draggableObject.getSettings())
            withContext(Dispatchers.Main) {
                reloadButton()
            }
        }
    }

    private fun delete(examName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            MainActivity.examTableManager.delete(examName)
        }
    }

    private fun settingIsChanged(): Boolean {
        val currentSetting = draggableObject.getSettings()
        val checkedExam = getCheckedExam()
        return run {
            val databaseSetting = MainActivity.examTableManager.get(checkedExam).subjects.map { it.name }
            currentSetting != databaseSetting
        }
    }

    private fun reloadDayExam(dayName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val subjects = MainActivity.examTableManager.get(dayName).subjects
            withContext(Dispatchers.Main) {
                draggableObject.reloadTableWithObjects(subjects.map { it.name })
            }
        }
    }

    private fun reloadButton() {
//        if(getCheckedExamView() == null) {
//            binding.findViewById<AppCompatButton>(R.id.save_button).isEnabled = isChanged
//        }
        CoroutineScope(Dispatchers.IO).launch {
            val isChanged = settingIsChanged()
            withContext(Dispatchers.Main) {
                binding.findViewById<AppCompatButton>(R.id.save_button).isEnabled = isChanged
                binding.findViewById<AppCompatButton>(R.id.init_button).isEnabled = isChanged
            }
        }
    }
}