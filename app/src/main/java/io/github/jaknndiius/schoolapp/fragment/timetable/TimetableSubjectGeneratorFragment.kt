package io.github.jaknndiius.schoolapp.fragment.timetable

import android.os.Bundle
import android.util.Log
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

class TimetableSubjectGeneratorFragment(
    private val parent: TimetableFragment
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = inflater.inflate(R.layout.fragment_timetable_setting_subject_generator, container, false)

        binding.findViewById<Button>(R.id.back_button).setOnClickListener {
            parent.openSubjectManagement(TimetableFragment.Direction.PREVIOUS)
        }

        binding.findViewById<Button>(R.id.generate_subject_button).setOnClickListener {
            val subjectEditText: EditText = binding.findViewById(R.id.subject_name)
            val teacherEditText: EditText = binding.findViewById(R.id.teacher_name)
            if(subjectEditText.text.isNullOrBlank()) {
                Toast.makeText(this.context, "과목 이름을 작성해 주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if(teacherEditText.text.isNullOrBlank()) {
                Toast.makeText(this.context, "선생님 성함을 작성해 주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val subjectName = subjectEditText.text.toString().replace(" ", "")
            val teacherName = teacherEditText.text.toString().replace(" ", "")
            CoroutineScope(Dispatchers.IO).launch {
                if(MainActivity.subjectManager.isExist(subjectName)) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@TimetableSubjectGeneratorFragment.context, "이미 존재하는 과목이름입니다.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    MainActivity.subjectManager.define(subjectName, teacherName)
                    MainActivity.subjectManager.getAll().forEach { Log.d("KAJ:LKSJL:", it.toString()) }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@TimetableSubjectGeneratorFragment.context, "'${subjectName}' 과목을 생성했습니다.", Toast.LENGTH_LONG).show()
                        parent.openSubjectManagement(TimetableFragment.Direction.PREVIOUS)
                    }
                }
            }
        }

        return binding
    }
}