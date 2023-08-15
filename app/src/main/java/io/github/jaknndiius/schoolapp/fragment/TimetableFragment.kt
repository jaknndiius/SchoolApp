package io.github.jaknndiius.schoolapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.database.Subject
import io.github.jaknndiius.schoolapp.enum.Direction
import io.github.jaknndiius.schoolapp.fragment.timetable.*

class TimetableFragment : Fragment(), MainFragment {

    lateinit var currentFragment: Fragment
    private lateinit var binding: View


    private fun changeFragment(fragment: Fragment, direction: Direction) {

        currentFragment = fragment
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        if(direction == Direction.NEXT) {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.fade_out)
        } else if(direction == Direction.PREVIOUS) {
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.exit_to_right)
        } else if(direction == Direction.NEXT_VERTICAL) {
            transaction.setCustomAnimations(R.anim.enter_from_below, R.anim.fade_out)
        } else if(direction == Direction.PREVIOUS_VERTICAL) {
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.exit_to_below)
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
    fun openSubjectEditor(direction: Direction, currentSubject: Subject? = null) {
        changeFragment(SubjectEditorFragment(this, currentSubject), direction)
    }
    fun openTimetableChanger(direction: Direction) {
        changeFragment(ChangeTimetableFragment(this), direction)
    }
    fun openExamSetter(direction: Direction) {
        changeFragment(ExamSetterFragment(this), direction)
    }

    fun openExamUpdater(direction: Direction, subject: Subject) {
        changeFragment(ExamUpdaterFragment(this, subject), direction)
    }

    fun openExamTimetableChanger(direction: Direction) {
        changeFragment(ExamTimetableChangerFragment(this), direction)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflate(R.layout.fragment_timetable, container, false)
        openTimetableList(Direction.NONE)
        return binding
    }

    override fun changeHeader(offset: Float, direction: Direction) {

        val title: TextView = binding.findViewById(R.id.title)

        if(direction == Direction.NONE) {
            title.alpha = 1-offset
        } else if(direction == Direction.PREVIOUS) {
            title.alpha = offset
        }
        if(currentFragment is ListFragment) {
            val listFragment = currentFragment as ListFragment

            listFragment.view?.findViewById<BottomNavigationView>(R.id.bottom_navbar)?.let {
                if(direction == Direction.NONE) {
                    it.translationY = it.height * offset
                } else if(direction == Direction.PREVIOUS) {
                    it.translationY = it.height - (it.height * offset)
                }
            }

            if(listFragment.mode != 0) return

            val scrollView:HorizontalScrollView? = listFragment.view?.findViewById(R.id.tables_scroll)
            val x = listFragment.getScrollX()
            if(direction == Direction.NONE) {
                scrollView?.scrollX = x -(x * offset).toInt()
            } else if(direction == Direction.PREVIOUS) {
                scrollView?.scrollX = (x * offset).toInt()
            }
        }
    }

}