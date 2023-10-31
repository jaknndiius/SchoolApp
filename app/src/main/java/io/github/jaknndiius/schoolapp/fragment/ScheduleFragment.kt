package io.github.jaknndiius.schoolapp.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.preset.Direction
import io.github.jaknndiius.schoolapp.fragment.schedule.ListFragment
import io.github.jaknndiius.schoolapp.fragment.schedule.ScheduleGeneratorFragment
import io.github.jaknndiius.schoolapp.fragment.timetable.*
import io.github.jaknndiius.schoolapp.preset.ScheduleFragmentType
import io.github.jaknndiius.schoolapp.preset.TimetableFragmentType

class ScheduleFragment : Fragment(), MainFragment {

    private lateinit var fragments: Map<ScheduleFragmentType, Fragment>
    private lateinit var currentFragment: Fragment
    lateinit var binding: View

    private fun changeFragment(fragment: Fragment, direction: Direction, backStack: Boolean = true) {

        currentFragment = fragment
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        when(direction) {
            Direction.NEXT -> transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.fade_out)
            Direction.PREVIOUS -> transaction.setCustomAnimations(R.anim.fade_in, R.anim.exit_to_right)
            Direction.NEXT_VERTICAL -> transaction.setCustomAnimations(R.anim.enter_from_below, R.anim.fade_out)
            Direction.PREVIOUS_VERTICAL -> transaction.setCustomAnimations(R.anim.fade_in, R.anim.exit_to_below)
            else -> transaction
        }.replace(R.id.schedule_container, fragment).run {
            if(backStack) addToBackStack(null)
            commit()
        }
    }

    fun openScheduleList(direction: Direction) {
        changeFragment(fragments[ScheduleFragmentType.LIST_FRAGMENT]!!, direction, false)
    }

    fun openScheduleGenerator(direction: Direction) {
        changeFragment(fragments[ScheduleFragmentType.SCHEDULE_GENERATOR]!!, direction)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflater.inflate(R.layout.fragment_schedule, container, false)

        fragments = mapOf(
            ScheduleFragmentType.LIST_FRAGMENT to ListFragment(this),
            ScheduleFragmentType.SCHEDULE_GENERATOR to ScheduleGeneratorFragment(this)
        )

//        openScheduleList(Direction.NONE)
        openScheduleGenerator(Direction.NONE)
        return binding
    }

    override fun changeHeader(offset: Float, direction: Direction) {
        val header: LinearLayout = binding.findViewById(R.id.headerBackground)
        val title: TextView = binding.findViewById(R.id.title)
        if(direction == Direction.NONE) {
            val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                100 + 210*offset,
                resources.displayMetrics
            ).toInt()
            header.layoutParams = ViewGroup.LayoutParams(header.layoutParams.width, px)

            title.alpha = 1-offset
        } else if(direction == Direction.PREVIOUS) {
            title.alpha = offset
        }

    }
}