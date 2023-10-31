package io.github.jaknndiius.schoolapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.database.Subject
import io.github.jaknndiius.schoolapp.preset.Direction
import io.github.jaknndiius.schoolapp.fragment.timetable.*
import io.github.jaknndiius.schoolapp.preset.TimetableFragmentType

class TimetableFragment : Fragment(), MainFragment {

    private lateinit var binding: View
    private lateinit var fragments: Map<TimetableFragmentType, Fragment>
    lateinit var currentFragment: Fragment

    private fun changeFragment(fragment: Fragment, direction: Direction, backStack: Boolean = true) {

        currentFragment = fragment
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        when(direction) {
            Direction.NEXT -> transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.fade_out)
            Direction.PREVIOUS -> transaction.setCustomAnimations(R.anim.fade_in, R.anim.exit_to_right)
            Direction.NEXT_VERTICAL -> transaction.setCustomAnimations(R.anim.enter_from_below, R.anim.fade_out)
            Direction.PREVIOUS_VERTICAL -> transaction.setCustomAnimations(R.anim.fade_in, R.anim.exit_to_below)
            else -> transaction
        }.replace(R.id.timetable_container, fragment).run {
            if(backStack) addToBackStack(null)
            commit()
        }
    }

    fun openTimetableList(direction: Direction) {
//        changeFragment(ListFragment(this), direction, false)
        changeFragment(fragments[TimetableFragmentType.LIST_FRAGMENT]!!, direction, false)

    }
    fun openTimetableSetting(direction: Direction) {
//        changeFragment(SettingFragment(this), direction)
        changeFragment(fragments[TimetableFragmentType.SETTING_FRAGMENT]!!, direction)
    }
    fun openSubjectManagement(direction: Direction) {
//        changeFragment(ManageSubjectFragment(this), direction)
        changeFragment(fragments[TimetableFragmentType.MANAGE_SUBJECT_FRAGMENT]!!, direction)

    }
    fun openSubjectEditor(direction: Direction, currentSubject: Subject? = null) {
        changeFragment(SubjectEditorFragment(this, currentSubject), direction)
    }
    fun openTimetableChanger(direction: Direction) {
//        changeFragment(ChangeTimetableFragment(this), direction)
        changeFragment(fragments[TimetableFragmentType.CHANGE_TIMETABLE_FRAGMENT]!!, direction)

    }
    fun openExamSetter(direction: Direction) {
//        changeFragment(ExamSetterFragment(this), direction)
        changeFragment(fragments[TimetableFragmentType.EXAM_SETTER_FRAGMENT]!!, direction)
    }


    fun openExamUpdater(direction: Direction, subject: Subject) {
        changeFragment(ExamUpdaterFragment(this, subject), direction)
    }

    fun openExamTimetableChanger(direction: Direction) {
//        changeFragment(ExamTimetableChangerFragment(this), direction)
        changeFragment(fragments[TimetableFragmentType.EXAM_TIMETABLE_CHANGER_FRAGMENT]!!, direction)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflater.inflate(R.layout.fragment_timetable, container, false)

        fragments = mapOf(
            TimetableFragmentType.LIST_FRAGMENT to ListFragment(this),
            TimetableFragmentType.SETTING_FRAGMENT to SettingFragment(this),
            TimetableFragmentType.MANAGE_SUBJECT_FRAGMENT to ManageSubjectFragment(this),
//            TimetableFragmentType.SUBJECT_EDITOR_FRAGMENT to SubjectEditorFragment(this),
            TimetableFragmentType.CHANGE_TIMETABLE_FRAGMENT to ChangeTimetableFragment(this),
            TimetableFragmentType.EXAM_SETTER_FRAGMENT to ExamSetterFragment(this),
//            TimetableFragmentType.EXAM_UPDATER_FRAGMENT to ExamUpdaterFragment(this),
            TimetableFragmentType.EXAM_TIMETABLE_CHANGER_FRAGMENT to ExamTimetableChangerFragment(this)
        )


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
        }
    }

}