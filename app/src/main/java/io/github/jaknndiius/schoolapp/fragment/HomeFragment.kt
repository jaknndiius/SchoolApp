package io.github.jaknndiius.schoolapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.preset.Direction

class HomeFragment : Fragment(), MainFragment {

    lateinit var binding: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflate(R.layout.fragment_home, container, false)

        binding.findViewById<AppCompatButton>(R.id.camera).setOnClickListener {

        }

        return binding
    }

    override fun changeHeader(offset: Float, direction: Direction) {
        val title: TextView = binding.findViewById(R.id.title)
        val subtitle: TextView = binding.findViewById(R.id.subtitle)
        title.alpha = 1-offset*2
        subtitle.alpha = 1-offset*2
    }
}