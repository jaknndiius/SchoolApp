package io.github.jaknndiius.schoolapp.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jaknndiius.schoolapp.R

class PhotoTypeDialog(
    context: Context?,
    private val run: (index: Int) -> Unit
): AlertDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photo_type_dialog_layout)

        window?.setBackgroundDrawableResource(R.drawable.transparent)

        findViewById<BottomNavigationView>(R.id.select_type).setOnItemSelectedListener { menuItem ->
            run(if(menuItem.itemId == R.id.menu_perf) 0 else 1)
            dismiss()
            true
        }

    }
}