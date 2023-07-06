package io.github.jaknndiius.schoolapp.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.enums.Direction

class HomeFragment : Fragment(), MainFragment {

    lateinit var binding: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflate(R.layout.fragment_home, container, false)
        return binding
    }

    override fun changeHeader(offset: Float, direction: Direction) {
        val title: TextView = binding.findViewById(R.id.title)
        val subtitle: TextView = binding.findViewById(R.id.subtitle)
        title.alpha = 1-offset*2
        subtitle.alpha = 1-offset*2
    }
}