package io.github.jaknndiius.schoolapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jaknndiius.schoolapp.fragment.BiteCalculatorFragment
import io.github.jaknndiius.schoolapp.fragment.HomeFragment
import io.github.jaknndiius.schoolapp.fragment.ScheduleFragment
import io.github.jaknndiius.schoolapp.fragment.TimetableFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideActionBar()

        val home = HomeFragment()
        val timetable = TimetableFragment()
        val schedule = ScheduleFragment()
        val biteCalcultor = BiteCalculatorFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, home).commit()

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.menu_home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, home).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_timetable -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, timetable).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_schedule -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, schedule).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_bite_calculator -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, biteCalcultor).commit()
                    return@setOnNavigationItemSelectedListener true
                }
            }

            false
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        hideActionBar()
        super.onWindowFocusChanged(hasFocus)
    }

    fun hideActionBar() {
        supportActionBar?.hide()
    }
}