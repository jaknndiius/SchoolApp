package io.github.jaknndiius.schoolapp.fragment.timetable

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.fragment.TimetableFragment

class TimetableSettingFragment(
    private val parent: TimetableFragment
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_timetable_setting, container, false)
        binding.findViewById<AppCompatButton>(R.id.back_button).setOnClickListener {
            parent.openTimetableList(TimetableFragment.Direction.PREVIOUS)
        }
        binding.findViewById<Button>(R.id.manage_subject_button).setOnClickListener {
            parent.openSubjectManagement(TimetableFragment.Direction.NEXT)
        }
        return binding
    }
}