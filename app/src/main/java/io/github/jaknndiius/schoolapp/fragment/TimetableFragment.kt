package io.github.jaknndiius.schoolapp.fragment

import android.annotation.SuppressLint
import android.icu.util.LocaleData
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceControl.Transaction
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.database.Subject
import io.github.jaknndiius.schoolapp.database.SubjectTable
import io.github.jaknndiius.schoolapp.fragment.timetable.TimetableListFragment
import io.github.jaknndiius.schoolapp.fragment.timetable.TimetableManageSubjectFragment
import io.github.jaknndiius.schoolapp.fragment.timetable.TimetableSettingFragment
import io.github.jaknndiius.schoolapp.fragment.timetable.TimetableSubjectGeneratorFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Date

class TimetableFragment() : Fragment() {
    private val weekday = listOf("월", "화", "수", "목", "금")

    private lateinit var currentFragment: Fragment;

    public enum class Direction{NEXT, PREVIOUS, NONE}
    private fun changeFragment(fragment: Fragment, direction: Direction) {

        currentFragment = fragment
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        if(direction == Direction.NEXT) {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.fade_out)
        } else if(direction == Direction.PREVIOUS) {
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.exit_to_right)
        }
        transaction.replace(R.id.timetable_container, fragment).addToBackStack(null).commit()
    }

    fun openTimetableList(direction: Direction) {
        changeFragment(TimetableListFragment(this), direction)
    }
    fun openTimetableSetting(direction: Direction) {
        changeFragment(TimetableSettingFragment(this), direction)
    }
    fun openSubjectManagement(direction: Direction) {
        changeFragment(TimetableManageSubjectFragment(this), direction)
    }
    fun openSubjectGenerator(direction: Direction) {
        changeFragment(TimetableSubjectGeneratorFragment(this), direction)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_timetable, container, false)
        openTimetableList(Direction.NONE)
        return binding
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun reload() {
        if(currentFragment is TimetableListFragment) (currentFragment as TimetableListFragment).reload()
    }

}