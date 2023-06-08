package io.github.jaknndiius.schoolapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jaknndiius.schoolapp.database.DatabaseHelper
import io.github.jaknndiius.schoolapp.fragment.*

class MainActivity : AppCompatActivity() {

    private val fragmentId = mapOf(0 to R.id.menu_home, 1 to R.id.menu_timetable, 2 to R.id.menu_schedule, 3 to R.id.menu_bite_calculator)

    private var dbHelper: DatabaseHelper? = null
    private var database: SQLiteDatabase? = null
    private var databaseName = "MyDatabase"
    private var tableName = "Timetable"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideActionBar()

        val pager: ViewPager = findViewById(R.id.pager)
        val adapter: Adapter = Adapter(supportFragmentManager)
        adapter.addItems(listOf(HomeFragment(), TimetableFragment(), ScheduleFragment(), BiteCalculatorFragment()))
        pager.adapter = adapter
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

        pager.addOnPageChangeListener(object: OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }

            override fun onPageSelected(position: Int) {
                if(fragmentId[position] == R.id.menu_timetable) (adapter.getItem(position) as TimetableFragment).reload()
                bottomNavigation.menu.findItem(fragmentId[position]?: R.id.menu_home).isChecked = true
            }

            override fun onPageScrollStateChanged(state: Int) { }

        })
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            pager.currentItem = fragmentId.filter { it.value == menuItem.itemId }.keys.first()
            true
        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        hideActionBar()
        super.onWindowFocusChanged(hasFocus)
    }

    private fun hideActionBar() { supportActionBar?.hide() }

    class Adapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        private val items = ArrayList<Fragment>()
        override fun getCount(): Int {
            return items.size
        }
        override fun getItem(position: Int): Fragment {
            return items[position]
        }
        fun addItems(items: Collection<Fragment>) {
            this.items.addAll(items)
        }
    }
}