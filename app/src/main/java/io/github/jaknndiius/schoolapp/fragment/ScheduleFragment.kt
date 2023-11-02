package io.github.jaknndiius.schoolapp.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.preset.Direction
import io.github.jaknndiius.schoolapp.fragment.schedule.ListFragment
import io.github.jaknndiius.schoolapp.fragment.schedule.ScheduleGeneratorFragment
import io.github.jaknndiius.schoolapp.fragment.timetable.*
import io.github.jaknndiius.schoolapp.preset.ScheduleFragmentType
import io.github.jaknndiius.schoolapp.preset.TimetableFragmentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleFragment : Fragment(), MainFragment {

    private lateinit var fragments: Map<ScheduleFragmentType, Fragment>
    private lateinit var currentFragment: Fragment
    lateinit var binding: View

    private fun changeFragment(fragment: Fragment, direction: Direction) {

        currentFragment = fragment
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        when(direction) {
            Direction.NEXT -> transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.fade_out)
            Direction.PREVIOUS -> transaction.setCustomAnimations(R.anim.fade_in, R.anim.exit_to_right)
            Direction.NEXT_VERTICAL -> transaction.setCustomAnimations(R.anim.enter_from_below, R.anim.fade_out)
            Direction.PREVIOUS_VERTICAL -> transaction.setCustomAnimations(R.anim.fade_in, R.anim.exit_to_below)
            else -> transaction
        }.replace(R.id.schedule_container, fragment).commit()
    }

    fun openScheduleList(direction: Direction) {
        CoroutineScope(Dispatchers.IO).launch {
            val schedules = MainActivity.scheduleManager.getAll()
            withContext(Dispatchers.Main) {
                if(schedules.isEmpty()) {
                    openScheduleGenerator(Direction.NONE)
                    if(direction != Direction.NONE) Toast.makeText(context, "저장된 일정이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                else changeFragment(fragments[ScheduleFragmentType.LIST_FRAGMENT]!!, direction)
            }
        }
    }

    fun openScheduleGenerator(direction: Direction) {
        changeFragment(ScheduleGeneratorFragment(this), direction)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflater.inflate(R.layout.fragment_schedule, container, false)

        fragments = mapOf(
            ScheduleFragmentType.LIST_FRAGMENT to ListFragment(this)
        )

        openScheduleList(Direction.NONE)
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