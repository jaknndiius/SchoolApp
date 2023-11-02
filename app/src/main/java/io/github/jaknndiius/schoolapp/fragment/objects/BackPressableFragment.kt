package io.github.jaknndiius.schoolapp.fragment.objects

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

abstract class BackPressableFragment: Fragment() {

    abstract fun onPressBack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { onPressBack() }
        })
    }

}