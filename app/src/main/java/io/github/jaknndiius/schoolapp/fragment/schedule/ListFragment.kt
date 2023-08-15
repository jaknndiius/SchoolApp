package io.github.jaknndiius.schoolapp.fragment.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.R

class ListFragment(
) : Fragment() {

    private lateinit var binding: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflater.inflate(R.layout.schedule_list, container, false)

        fill(inflater)
        return binding
    }

    private fun fill(inflater: LayoutInflater) {
        val scheduleLayout: LinearLayout = binding.findViewById(R.id.schedules_layout)

        listOf("AAAAAA", "BBBBBBBB", "CCCCCCCCC").forEach {
            scheduleLayout.addView(
                makeA(inflater, scheduleLayout, it)
            )
        }
    }

    private fun makeA(inflater: LayoutInflater, container: ViewGroup, t:String): View {
        return inflater.inflate(R.layout.schedule_list_element, container, false).apply {
            findViewById<TextView>(R.id.date).text = "TE" + t
        }
    }

}