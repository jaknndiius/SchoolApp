package io.github.jaknndiius.schoolapp.fragment.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.fragment.TimetableFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubjectGeneratorFragment(
    private val timetableFragment: TimetableFragment
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = inflater.inflate(R.layout.timetable_setting_subject_generator, container, false)

        binding.findViewById<Button>(R.id.back_button).setOnClickListener {
            timetableFragment.openSubjectManagement(TimetableFragment.Direction.PREVIOUS)
        }

        binding.findViewById<Button>(R.id.generate_subject_button).setOnClickListener {
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
                        Toast.makeText(this@SubjectGeneratorFragment.context, getString(R.string.say_already_existing_subject), Toast.LENGTH_LONG).show()
                    }
                } else {
                    MainActivity.subjectManager.define(subjectName, teacherName)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SubjectGeneratorFragment.context, getString(R.string.say_generated_subject_with_name, subjectName), Toast.LENGTH_LONG).show()
                        timetableFragment.openSubjectManagement(TimetableFragment.Direction.PREVIOUS)
                    }
                }
            }
        }

        return binding
    }
}