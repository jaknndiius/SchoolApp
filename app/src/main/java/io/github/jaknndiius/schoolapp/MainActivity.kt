package io.github.jaknndiius.schoolapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
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
import io.github.jaknndiius.schoolapp.database.manager.ScheduleManager
import io.github.jaknndiius.schoolapp.database.manager.SubjectManager
import io.github.jaknndiius.schoolapp.database.manager.SubjectTableManager
import io.github.jaknndiius.schoolapp.preset.Direction
import io.github.jaknndiius.schoolapp.fragment.*
import io.github.jaknndiius.schoolapp.fragment.timetable.ListFragment
import io.github.jaknndiius.schoolapp.notification.Alarm
import io.github.jaknndiius.schoolapp.notification.Notification
import io.github.jaknndiius.schoolapp.preset.RangeType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val fragmentId = mapOf(0 to R.id.menu_home, 1 to R.id.menu_timetable, 2 to R.id.menu_schedule, 3 to R.id.menu_bite_calculator)

    private val SUBJECT_DATABASE = "subject-database"
    private val SUBJECT_TABLE_DATABASE = "subject-table-database"
    private val EXAM_TABLE_DATABASE = "exam-table-database"
    private val SCHEDULE_DATABASE = "schedule-database"

    lateinit var adapter: Adapter
    lateinit var pager: ViewPager

    companion object {
        lateinit var subjectManager: SubjectManager
        lateinit var subjectTableManager: SubjectTableManager
        lateinit var examTableManager: ExamTableManager
        lateinit var scheduleManager: ScheduleManager
        lateinit var photoManager: Photo
        lateinit var notificationManager: Notification
        lateinit var alarmManager: Alarm
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideActionBar()

        loadDatabase()
        photoManager = Photo(this)
        notificationManager = Notification(this)
        alarmManager = Alarm(this)
        alarmManager.alert()

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

        val scheduleDatabase = Room.databaseBuilder(
            applicationContext,
            ScheduleDatabase::class.java, SCHEDULE_DATABASE
        ).build()
        val scheduleDao = scheduleDatabase.scheduleDao()
        scheduleManager = ScheduleManager(scheduleDao)

        CoroutineScope(Dispatchers.IO).launch {

            subjectManager.define("독서1", "윤동희")
            subjectManager.define("독서2", "신치훈")
            subjectManager.define("독서3", "김병호")
            subjectManager.define("인상", "윤동희")

            subjectManager.define("영어1", "장인석")
            subjectManager.define("영어2",  "이성훈")
            subjectManager.define("영어3", "장인석")

            subjectManager.define("수학1", "노현태")
            subjectManager.define("수학2", "박선주")
            subjectManager.define("수학3", "박선주")

            subjectManager.define("물리", "황준식")
            subjectManager.define("지구과학", "정희찬")

            subjectManager.define("음악", "지세현")
            subjectManager.define("체육", "박영덕")
            subjectManager.define("한국사", "김진영")
            subjectManager.define("미술", "권유정")
            subjectManager.define("일본어", "김희인")

            subjectManager.define("창체", "장인석")

            subjectTableManager.addOrUpdate(WeekDay.MONDAY,
                listOf("독서1", "영어2", "수학1", "창체", "수학2", "미술", "지구과학")
            )
            subjectTableManager.addOrUpdate(WeekDay.TUESDAY,
                listOf("물리", "물리", "수학3", "체육", "인상", "독서2", "영어1")
            )
            subjectTableManager.addOrUpdate(WeekDay.WENDESDAY,
                listOf("한국사", "수학1", "음악", "수학2", "창체", "창체", "창체")
            )
            subjectTableManager.addOrUpdate(WeekDay.THURSDAY,
                listOf("독서2", "수학2", "지구과학", "지구과학", "독서1", "일본어", "영어3")
            )
            subjectTableManager.addOrUpdate(WeekDay.FRIDAY,
                listOf("영어1", "독서3", "영어2", "창체", "물리", "수학1", "미술")
            )

            subjectManager.define("확률과 통계", "-")
            subjectManager.attachExam("확률과 통계", ExamAttr(listOf(18,4)).apply {
                ranges = listOf(RangeType.TEXTBOOK to "p10~59, p74~76제외", RangeType.PAPER to "240문제")
            })

            examTableManager.addOrUpdate("첫째날(10.5 목)",
                listOf("확률과 통계")
            )
            subjectManager.define("독서", "-")
            subjectManager.attachExam("독서", ExamAttr(listOf(22,4)).apply {
                ranges = listOf(RangeType.TEXTBOOK to "p74~79", RangeType.MOCK to "2021~2022년 2학년 3,6월", RangeType.MOCK to "2020년 이전 일부")
            })
            examTableManager.addOrUpdate("둘째날(10.6 금)",
                listOf("독서")
            )
            subjectManager.define("미적분", "-")
            subjectManager.attachExam("미적분", ExamAttr(listOf(16,4)).apply {
                ranges = listOf(RangeType.TEXTBOOK to "p10~99", RangeType.PAPER to "100문제(46, 97 제외)")
            })
            subjectManager.attachExam("한국사", ExamAttr(listOf(20,3)).apply {
                ranges = listOf(RangeType.TEXTBOOK to "p231~303(p294~297 제외)", RangeType.PAPER to "IV. p1~7")
            })
            examTableManager.addOrUpdate("셋째날(10.10 화)",
                listOf("미적분", "한국사")
            )
            subjectManager.define("영어", "-")
            subjectManager.attachExam("영어", ExamAttr(listOf(16,4)).apply {
                ranges = listOf(RangeType.TEXTBOOK to "1과~3과", RangeType.SUBBOOK to "Unit 13~17, Mid-test(p100~107), 유형독해모의고사 4회(p168~173)", RangeType.MOCK to "9월 18~45번", RangeType.OTHER to "서술형은 수업시간에 다룬 지문만")
            })
            examTableManager.addOrUpdate("넷째날(10.11 수)",
                listOf("영어")
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