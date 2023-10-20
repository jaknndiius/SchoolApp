package io.github.jaknndiius.schoolapp.fragment.timetable

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.preset.Direction
import io.github.jaknndiius.schoolapp.fragment.*
import io.github.jaknndiius.schoolapp.fragment.timetable.list.ExamTableFragment
import io.github.jaknndiius.schoolapp.fragment.timetable.list.PhotoFragment
import io.github.jaknndiius.schoolapp.fragment.timetable.list.RegularTableFragment
import io.github.jaknndiius.schoolapp.preset.InformationType

class ListFragment(
    private val timetableFragment: TimetableFragment
) : Fragment() {

    private lateinit var binding: View
    private lateinit var navbar: BottomNavigationView

    private var currentFragment : Fragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = inflater.inflate(R.layout.timetable_list, container, false)

        setTableToRegular()

        navbar = binding.findViewById(R.id.bottom_navbar)
        navbar.setOnNavigationItemSelectedListener { menuItem ->
            changeFragment(menuItem.itemId)
            true
        }

        binding.findViewById<AppCompatButton>(R.id.setting_button).setOnClickListener {
            timetableFragment.openTimetableSetting(Direction.NEXT)
        }

        return binding
    }

    private fun changeFragment(id: Int) {
        when(id) {
            R.id.menu_regular -> setTableToRegular()
            R.id.menu_exam -> setTableToExam()
            R.id.menu_photo -> setTableToPhoto()
        }
    }

    private fun setTableToRegular() {
        currentFragment = RegularTableFragment()
        fragmentManager?.beginTransaction()?.replace(R.id.container, currentFragment!!)?.commit()
    }

    private fun setTableToExam() {
        currentFragment = ExamTableFragment()
        fragmentManager?.beginTransaction()?.replace(R.id.container, currentFragment!!)?.commit()
    }

    private fun setTableToPhoto() {
        currentFragment = PhotoFragment()
        fragmentManager?.beginTransaction()?.replace(R.id.container, currentFragment!!)?.commit()
    }

    fun openPhotoFolder() {
        if(currentFragment is PhotoFragment) {
            MainActivity.photoManager.lastPhotoInformation?.run {
                (currentFragment as PhotoFragment).openDialog(subjectName, type)
            }
        }
    }

}