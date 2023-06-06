package io.github.jaknndiius.schoolapp.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.R
import java.nio.charset.Charset

class BiteCalculatorFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_bite_calculator, container, false)

        val stateLayout: LinearLayout = binding.findViewById(R.id.stateLinearLayout)
        val stringlength: TextView = stateLayout.findViewById<LinearLayout>(R.id.stringLinearLayout).findViewById(R.id.stringLength)
        val bitelength: TextView = stateLayout.findViewById<LinearLayout>(R.id.biteLinearLayout).findViewById(R.id.biteLength)

        val text: EditText = binding.findViewById(R.id.editTextTextMultiLine)
        text.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }

            override fun afterTextChanged(s: Editable) {
                val str = s.toString()
                stringlength.text = "${str.length}자"
                bitelength.text = "${str.toByteArray(Charset.defaultCharset()).size}"
            }
        })

        return binding.rootView
    }
}