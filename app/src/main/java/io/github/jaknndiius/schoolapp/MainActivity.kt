package io.github.jaknndiius.schoolapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.room.Room
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jaknndiius.schoolapp.camera.Photo
import io.github.jaknndiius.schoolapp.database.*
import io.github.jaknndiius.schoolapp.database.manager.ExamTableManager
import io.github.jaknndiius.schoolapp.database.manager.SubjectManager
import io.github.jaknndiius.schoolapp.database.manager.SubjectTableManager
import io.github.jaknndiius.schoolapp.preset.Direction
import io.github.jaknndiius.schoolapp.fragment.*
import io.github.jaknndiius.schoolapp.fragment.timetable.ListFragment
import io.github.jaknndiius.schoolapp.preset.RangeType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val fragmentId = mapOf(0 to R.id.menu_home, 1 to R.id.menu_timetable, 2 to R.id.menu_schedule, 3 to R.id.menu_bite_calculator)

    private val SUBJECT_DATABASE = "subject-database"
    private val SUBJECT_TABLE_DATABASE = "subject-table-database"
    private val EXAM_TABLE_DATABASE = "exam-table-database"

    lateinit var adapter: Adapter
    lateinit var pager: ViewPager

    companion object {
        lateinit var subjectManager: SubjectManager
        lateinit var subjectTableManager: SubjectTableManager
        lateinit var examTableManager: ExamTableManager
        lateinit var photoManager: Photo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideActionBar()

        loadDatabase()
        photoManager = Photo(this)

        pager = findViewById(R.id.pager)
        adapter = Adapter(supportFragmentManager)
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

    private fun hideActionBar() {
        supportActionBar?.hide()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    private fun loadDatabase() {

        val subjectDatabase = Room.databaseBuilder(
            applicationContext,
            SubjectDatabase::class.java, SUBJECT_DATABASE
        ).build()
        val subjectDao = subjectDatabase.subjectDao()
        subjectManager = SubjectManager(subjectDao)

        val subjectTableDatabase = Room.databaseBuilder(
            applicationContext,
            SubjectTableDatabase::class.java, SUBJECT_TABLE_DATABASE
        ).build()
        val subjectTableDao = subjectTableDatabase.subjectTableDao()
        subjectTableManager = SubjectTableManager(subjectTableDao, subjectManager)

        val examTableDatabase = Room.databaseBuilder(
            applicationContext,
            ExamTableDatabase::class.java, EXAM_TABLE_DATABASE
        ).build()
        val examTableDao = examTableDatabase.examTableDao()
        examTableManager = ExamTableManager(examTableDao, subjectManager)

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

            subjectManager.attachExam("영어1", ExamAttr(listOf(10,1)).apply {
                ranges = listOf(RangeType.TEXTBOOK to "p1~21", RangeType.PAPER to "12장", RangeType.SUBBOOK to "P3~12", RangeType.OTHER to "수업 첨부 파일 확인수업 첨부 파일 확인수업 첨부 파일 확인수업 첨부 파일 확인")
            })

            subjectManager.attachExam("영어2", ExamAttr(listOf(10,1)).apply {
                ranges = listOf(RangeType.TEXTBOOK to "범위는", RangeType.SUBBOOK to "부교재")

            })

            subjectManager.attachExam("문학3", ExamAttr(listOf(102,133)).apply {
                ranges = listOf(RangeType.TEXTBOOK to "범위는", RangeType.PAPER to "학습지")
            })

            subjectTableManager.addOrUpdate(WeekDay.MONDAY,
                listOf("문학1", "영어2", "수학1", "창체", "수학2", "음악", "지구과학")
            )
            subjectTableManager.addOrUpdate(WeekDay.TUESDAY,
                listOf("물리", "물리", "수학3", "체육", "인상", "문학2", "영어1")
            )
            subjectTableManager.addOrUpdate(WeekDay.WENDESDAY,
                listOf("한국사", "수학1", "미술", "수학2", "창체", "창체", "창체")
            )
            subjectTableManager.addOrUpdate(WeekDay.THURSDAY,
                listOf("문학2", "수학2", "지구과학", "지구과학", "문학1", "일본어", "영어3")
            )
            subjectTableManager.addOrUpdate(WeekDay.FRIDAY,
                listOf("영어1", "문학3", "영어2", "창체", "물리", "수학1", "음악")
            )

            examTableManager.addOrUpdate("첫째날",
                listOf("영어1", "문학3", "영어2")
            )

            examTableManager.addOrUpdate("둘째날",
                listOf("문학2", "수학2", "지구과학", "지구과학")
            )

        }
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == Photo.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val current = adapter.getItem(pager.currentItem)
            if( current is TimetableFragment && current.currentFragment is ListFragment) {
                (current.currentFragment as ListFragment).openPhotoFolder()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}