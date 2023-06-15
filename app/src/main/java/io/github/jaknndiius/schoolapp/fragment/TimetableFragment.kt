package io.github.jaknndiius.schoolapp.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.fragment.timetable.*

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
        changeFragment(ListFragment(this), direction)
    }
    fun openTimetableSetting(direction: Direction) {
        changeFragment(SettingFragment(this), direction)
    }
    fun openSubjectManagement(direction: Direction) {
        changeFragment(ManageSubjectFragment(this), direction)
    }
    fun openSubjectGenerator(direction: Direction) {
        changeFragment(SubjectGeneratorFragment(this), direction)
    }
    fun openTimetableChanger(direction: Direction) {
        changeFragment(ChangeTimetableFragment(this), direction)
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
        if(currentFragment is ListFragment) (currentFragment as ListFragment).reload()
    }

}