package io.github.jaknndiius.schoolapp.fragment.timetable

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.database.Subject
import io.github.jaknndiius.schoolapp.preset.Direction
import io.github.jaknndiius.schoolapp.fragment.TimetableFragment
import io.github.jaknndiius.schoolapp.fragment.objects.BackPressableFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubjectEditorFragment(
    private val timetableFragment: TimetableFragment,
    private val currentSubject: Subject? = null
) : BackPressableFragment() {

    lateinit var binding: View

    override fun onPressBack() {
        timetableFragment.openSubjectManagement(Direction.PREVIOUS_VERTICAL)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflater.inflate(R.layout.timetable_setting_subject_editor, container, false)
        binding.findViewById<Button>(R.id.back_button).setOnClickListener {
            onPressBack()
        }
        if(currentSubject == null) setModeToGenerator()
        else setModeToEditor()
        return binding
    }

    private fun setModeToGenerator() {
        val title = resources.getString(R.string.setting_generate_subject)
        binding.findViewById<TextView>(R.id.title).text = title
        binding.findViewById<TextView>(R.id.subject_name).requestFocus()
        binding.findViewById<Button>(R.id.generate_subject_button).apply {
            text = title
        }.setOnClickListener {
            val subjectEditText: EditText = binding.findViewById(R.id.subject_name)
            val teacherEditText: EditText = binding.findViewById(R.id.teacher_name)
            if(subjectEditText.text.isNullOrBlank()) {
                Toast.makeText(this.context, getString(R.string.request_subject_name), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if(teacherEditText.text.isNullOrBlank()) {
                Toast.makeText(this.context, getString(R.string.request_teacher_name), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val subjectName = subjectEditText.text.toString().replace(" ", "")
            val teacherName = teacherEditText.text.toString().replace(" ", "")
            CoroutineScope(Dispatchers.IO).launch {
                if(MainActivity.subjectManager.isExist(subjectName)) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SubjectEditorFragment.context, getString(R.string.say_already_existing_subject), Toast.LENGTH_LONG).show()
                    }
                } else {
                    MainActivity.subjectManager.define(subjectName, teacherName)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SubjectEditorFragment.context, getString(R.string.say_generated_subject_with_name, subjectName), Toast.LENGTH_LONG).show()
                        timetableFragment.openSubjectManagement(Direction.PREVIOUS_VERTICAL)
                    }
                }
            }
        }
    }

    private fun setModeToEditor() {
        val title = resources.getString(R.string.setting_edit_subject)
        binding.findViewById<TextView>(R.id.title).text = title

        binding.findViewById<LinearLayout>(R.id.subject_name_layout).apply {
            setBackgroundResource(R.drawable.bg_white_c4_b2gray)

            findViewById<TextView>(R.id.subject_name_header)
                .setTextColor(resources.getColor(R.color.gray))

            findViewById<EditText>(R.id.subject_name).apply {
                isEnabled = false
                setText(currentSubject!!.name)
                setTextColor(resources.getColor(R.color.gray))
            }

        }

        binding.findViewById<EditText>(R.id.teacher_name).apply {
            setText(currentSubject!!.teacherName)
            setSelectAllOnFocus(true)
        }.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.findViewById<Button>(R.id.generate_subject_button).apply {
            text = title
        }.setOnClickListener {
            val teacherEditText: EditText = binding.findViewById(R.id.teacher_name)
            if(teacherEditText.text.isNullOrBlank()) {
                Toast.makeText(this.context, getString(R.string.request_teacher_name), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val teacherName = teacherEditText.text.toString().replace(" ", "")
            CoroutineScope(Dispatchers.IO).launch {
                MainActivity.subjectManager.define(currentSubject!!.name, teacherName)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SubjectEditorFragment.context, getString(R.string.say_edited_subject_with_name, currentSubject!!.name), Toast.LENGTH_LONG).show()
                    timetableFragment.openSubjectManagement(Direction.PREVIOUS_VERTICAL)
                }

            }
        }
    }
}