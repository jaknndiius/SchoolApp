package io.github.jaknndiius.schoolapp.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.enums.Direction

class ScheduleFragment : Fragment(), MainFragment {

    lateinit var binding: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflate(R.layout.fragment_schedule, container, false)
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