package io.github.jaknndiius.schoolapp.fragment.timetable

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.enums.Direction
import io.github.jaknndiius.schoolapp.fragment.TimetableFragment

class SettingFragment(
    private val timetableFragment: TimetableFragment
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.timetable_setting, container, false)
        binding.findViewById<AppCompatButton>(R.id.back_button).setOnClickListener {
            timetableFragment.openTimetableList(Direction.PREVIOUS)
        }
        binding.findViewById<Button>(R.id.manage_subject_button).setOnClickListener {
            timetableFragment.openSubjectManagement(Direction.NEXT_VERTICAL)
        }
        binding.findViewById<Button>(R.id.change_timetable_button).setOnClickListener {
            timetableFragment.openTimetableChanger(Direction.NEXT_VERTICAL)
        }
        binding.findViewById<Button>(R.id.set_exam_button).setOnClickListener {
            timetableFragment.openExamSetter(Direction.NEXT_VERTICAL)
        }
        return binding
    }
}