package io.github.jaknndiius.schoolapp.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.enum.Direction
import java.nio.charset.Charset

class BiteCalculatorFragment : Fragment(), MainFragment {

    lateinit var binding: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflate(R.layout.fragment_bite_calculator, container, false)

        val stateLayout: LinearLayout = binding.findViewById(R.id.stateLinearLayout)
        val stringlength: TextView = stateLayout.findViewById<LinearLayout>(R.id.stringLinearLayout).findViewById(R.id.stringLength)
        val bitelength: TextView = stateLayout.findViewById<LinearLayout>(R.id.biteLinearLayout).findViewById(R.id.biteLength)

        val text: EditText = binding.findViewById(R.id.editTextTextMultiLine)
        text.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }

            override fun afterTextChanged(s: Editable) {
                val str = s.toString()
                stringlength.text = getString(R.string.word_length_with_count, str.length)
                bitelength.text = "${str.toByteArray(Charset.defaultCharset()).size}"
            }
        })

        return binding.rootView
    }

    override fun changeHeader(offset: Float, direction: Direction) {
        val header: LinearLayout = binding.findViewById(R.id.headerBackground)
        val title: TextView = binding.findViewById(R.id.title)
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            100 + 210*offset,
            resources.displayMetrics
        ).toInt()
        header.layoutParams = ViewGroup.LayoutParams(header.layoutParams.width, px)

        title.alpha = offset
    }
}