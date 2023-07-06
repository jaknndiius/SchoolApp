package io.github.jaknndiius.schoolapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.room.Room
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jaknndiius.schoolapp.database.*
import io.github.jaknndiius.schoolapp.enums.Direction
import io.github.jaknndiius.schoolapp.fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val fragmentId = mapOf(0 to R.id.menu_home, 1 to R.id.menu_timetable, 2 to R.id.menu_schedule, 3 to R.id.menu_bite_calculator)

    private val SUBJECT_DATABASE = "subject-database"
    private val SUBJECT_TABLE_DATABASSE = "subject-table-database"

    lateinit var subjectDatabase: SubjectDatabase
    lateinit var subjectDao: SubjectDao

    lateinit var subjectTableDatabase: SubjectTableDatabase
    lateinit var subjectTableDao: SubjectTableDao

    companion object {
        lateinit var subjectManager: SubjectManager
        lateinit var subjectTableManager: SubjectTableManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideActionBar()

        subjectDatabase = Room.databaseBuilder(
            applicationContext,
            SubjectDatabase::class.java, SUBJECT_DATABASE
        ).build()
        subjectDao = subjectDatabase.subjectDao()

        subjectTableDatabase = Room.databaseBuilder(
            applicationContext,
            SubjectTableDatabase::class.java, SUBJECT_TABLE_DATABASSE
        ).build()
        subjectTableDao = subjectTableDatabase.subjectTableDao()

        subjectTableManager = SubjectTableManager()
        subjectManager = SubjectManager()
        CoroutineScope(Dispatchers.IO).launch {
            subjectManager.define("문학1", "윤동희")
            subjectManager.define("문학2", "신치훈")
            subjectManager.define("문학3", "김병호")
            subjectManager.define("인상", "윤동희")

            subjectManager.define("영어1", "장인석")
            subjectManager.define("영어2",  "이성훈")
            subjectManager.define("영어3", "장인석")


            subjectManager.define("수학1", "노현태")
            subjectManager.define("수학2", "박진우")
            subjectManager.define("수학3", "박진우")

            subjectManager.define("물리", "황준식")
            subjectManager.define("지구과학", "정희찬")

            subjectManager.define("음악", "지세현")
            subjectManager.define("체육", "박영덕")
            subjectManager.define("한국사", "김진영")
            subjectManager.define("미술", "권유정")
            subjectManager.define("일본어", "김희인")

            subjectManager.define("창체", "장인석")

            
            SubjectTableManager().addOrUpdate(WeekDay.MONDAY,
                listOf("문학1", "영어2", "수학1", "창체", "수학2", "음악", "지구과학")
            )
            SubjectTableManager().addOrUpdate(WeekDay.TUESDAY,
                listOf("물리", "물리", "수학3", "체육", "인상", "문학2", "영어1")
            )
            SubjectTableManager().addOrUpdate(WeekDay.WENDESDAY,
                listOf("한국사", "수학1", "미술", "수학2", "창체", "창체", "창체")
            )
            SubjectTableManager().addOrUpdate(WeekDay.THURSDAY,
                listOf("문학2", "수학2", "지구과학", "지구과학", "문학1", "일본어", "영어3")
            )
            SubjectTableManager().addOrUpdate(WeekDay.FRIDAY,
                listOf("영어1", "문학3", "영어2", "창체", "물리", "수학1", "음악")
            )
        }

        val pager: ViewPager = findViewById(R.id.pager)
        val adapter = Adapter(supportFragmentManager)
        adapter.addItems(listOf(HomeFragment(), TimetableFragment(), ScheduleFragment(), BiteCalculatorFragment()))
        pager.adapter = adapter
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        pager.addOnPageChangeListener(object: OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                try {
                    if(position == adapter.count-1) return
                    (adapter.getItem(position) as MainFragment).changeHeader(positionOffset, Direction.NONE)
                    (adapter.getItem(position +1) as MainFragment).changeHeader(positionOffset, Direction.PREVIOUS)
                }catch (_:Exception) {}

            }

            override fun onPageSelected(position: Int) {

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

    inner class SubjectManager {
        fun clear() {
            subjectDao.getAll().forEach { subjectDao.delete(it)  }
        }
        fun getAll(): List<Subject> {
            return subjectDao.getAll()
        }
        fun get(name: String): Subject {
            return subjectDao.getAll().first { it.name == name }
        }
        fun isExist(name: String): Boolean {
            return !subjectDao.getAll().none { it.name == name }
        }
        fun define(name: String, teacherName: String) {
            if(isExist(name)) subjectDao.updateSubjects(Subject(name, teacherName))
            else subjectDao.insertAll(Subject(name, teacherName))
        }
        fun delete(name: String) {
            if(!isExist(name)) return
            subjectDao.delete(get(name))
        }
    }
    inner class SubjectTableManager {
        fun clear() {
            subjectTableDao.getAll().forEach { subjectTableDao.delete(it)  }
        }
        fun getAll(): List<SubjectTable> {
            val newList: ArrayList<SubjectTable> = arrayListOf()
            for(weekDay in WeekDay.values()) {
                if(isExist(weekDay)) newList.add(findByWeekday(weekDay))
                else newList.add(SubjectTable(weekDay, listOf()))
            }
            return newList
        }
        private fun findByWeekday(weekDay: WeekDay): SubjectTable {
            return subjectTableDao.findById(weekDay)
        }
        fun get(weekDay: WeekDay): SubjectTable {
            return getAll()[weekDay.ordinal]
        }
        private fun isExist(weekDay: WeekDay): Boolean {
            return !subjectTableDao.getAll().none { it.uid == weekDay }
        }
        fun addOrUpdate(weekDay: WeekDay, subjectNames: List<String>) {
            val subjects = arrayListOf<Subject>()
            subjectNames.forEach {
                if(SubjectManager().isExist(it)) subjects.add(SubjectManager().get(it))
            }
            if(isExist(weekDay)) subjectTableDao.updateSubjectTables(SubjectTable(weekDay, subjects))
            else subjectTableDao.insertAll(SubjectTable(weekDay, subjects))
        }
        fun delete(weekDay: WeekDay) {
            if(!isExist(weekDay)) return
            subjectTableDao.delete(findByWeekday(weekDay))
        }
    }
}