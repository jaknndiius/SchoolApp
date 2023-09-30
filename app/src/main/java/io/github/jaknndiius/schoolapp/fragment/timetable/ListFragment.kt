package io.github.jaknndiius.schoolapp.fragment.timetable

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.camera.Photo
import io.github.jaknndiius.schoolapp.camera.data.Information
import io.github.jaknndiius.schoolapp.database.ExamTable
import io.github.jaknndiius.schoolapp.database.Subject
import io.github.jaknndiius.schoolapp.database.SubjectTable
import io.github.jaknndiius.schoolapp.dialog.FullscreenImgViewDialog
import io.github.jaknndiius.schoolapp.dialog.ImgViewDialog
import io.github.jaknndiius.schoolapp.enum.Direction
import io.github.jaknndiius.schoolapp.enum.InformationType
import io.github.jaknndiius.schoolapp.fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ListFragment(
    private val timetableFragment: TimetableFragment
) : Fragment() {

    private lateinit var weekday: Array<String>
    private lateinit var binding: View

    private lateinit var tableLayout: LinearLayout
    private lateinit var inflater: LayoutInflater

    private var currentClickedSubject: Information? = null
    private var currentDialog: ImgViewDialog? = null

    var mode: Int = 0
    // mode 0 -> regular || 1 -> exam || 2 -> photo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        weekday = resources.getStringArray(R.array.weekday_names)
        binding = inflater.inflate(R.layout.timetable_list, container, false)
        this.inflater = inflater
        val scrollView: HorizontalScrollView = binding.findViewById(R.id.tables_scroll)
        tableLayout = scrollView.findViewById(R.id.tables_layout)

        binding.findViewById<BottomNavigationView>(R.id.bottom_navbar).setOnNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.menu_regular -> {
                    setTableToRegular()
                }
                R.id.menu_exam -> {
                    setTableToExam()
                }
                R.id.menu_photo -> {
                    setTableToPhoto()
                }
            }
            true
        }

        setTableToRegular()

        val settingButton: AppCompatButton = binding.findViewById(R.id.setting_button)
        settingButton.setOnClickListener {
            timetableFragment.openTimetableSetting(Direction.NEXT)
        }

        return binding
    }

    private fun setTableToRegular() {
        mode = 0
        CoroutineScope(Dispatchers.IO).launch {
            val sortTables = MainActivity.subjectTableManager.getAll().sortedBy { it.uid }
            withContext(Dispatchers.Main) {
                tableLayout.removeAllViews()
                for(table in sortTables) {
                    tableLayout.addView(makeDay(tableLayout, table))
                }

                Handler().postDelayed({reload()}, 10)
            }
        }
    }

    private fun setTableToExam() {
        mode = 1
        CoroutineScope(Dispatchers.IO).launch {
            val examTables = MainActivity.examTableManager.getAll()

            examTables.maxOfOrNull { it.subjects.size }?.let { max ->
                examTables.forEach {
                    if(it.subjects.size == max) return@forEach

                    val newlist = ArrayList(it.subjects)
                    while (newlist.size != max) {
                        newlist.add(Subject("", ""))
                    }
                    it.subjects = newlist
                }
            }


            withContext(Dispatchers.Main) {
                tableLayout.removeAllViews()
                for(table in examTables) {
                    tableLayout.addView(makeExam(tableLayout, table))
                }
                binding.findViewById<HorizontalScrollView>(R.id.tables_scroll).scrollX = 0
            }
        }
    }

    private fun setTableToPhoto() {
        mode = 2
        tableLayout.removeAllViews()
        TODO("여기 사진 관련 추가해야함.")
    }

    private fun getDay(): Int {
        // Sunday:0 ~ Saturday: 6
        return Date().day.coerceAtMost(5).coerceAtLeast(1)
    }
    fun getScrollX(): Int {
        return 700*(getDay() -1)
    }

    private fun reload() {
        if(mode != 0) return
        try {
            binding.findViewById<HorizontalScrollView>(R.id.tables_scroll).scrollX = getScrollX()
        }catch (e: Exception) {Log.d("EEEEEEE", e.message.toString()) }
    }

    private fun makeDay(container: ViewGroup, table: SubjectTable): View {
        val view = inflater.inflate(R.layout.timetable_day_layout, container, false)
        view.findViewById<TextView>(R.id.day_name).text = "${weekday[table.uid.ordinal]}요일"
        val subjectLayout: LinearLayout = view.findViewById(R.id.subject_layout)
        for(subject in table.subjects) {
            subjectLayout.addView(
                makeSubject(subjectLayout, subject.name, subject.teacherName?: "ERROR")
            )
        }
        return view
    }
    private fun makeExam(container: ViewGroup, table: ExamTable): View {
        val view = inflater.inflate(R.layout.timetable_day_layout, container, false)
        view.findViewById<TextView>(R.id.day_name).text = table.name
        val subjectLayout: LinearLayout = view.findViewById(R.id.subject_layout)
        for(subject in table.subjects) {
            subjectLayout.addView(
                makeSubject(subjectLayout, subject.name, subject.teacherName?: "ERROR")
            )
        }
        return view
    }

    private fun makeSubject(container: ViewGroup, subjectName: String, teacherName: String): View {
        return inflater.inflate(R.layout.timetable_subject_layout, container, false).apply {
            findViewById<TextView>(R.id.subject_name).text = subjectName
            findViewById<TextView>(R.id.teacher_name).text = teacherName
            setOnClickListener {
                val type = when(mode) {
                    // TODO REMOVE
                    0  -> InformationType.PERFORMANCE
                    1 -> InformationType.EXAM
                    else -> InformationType.PERFORMANCE
                }
                openDialog(type, subjectName)
            }
        }
    }

    private fun openDialog(type: InformationType, subjectName: String) {
        val mainActivity = activity as MainActivity

        currentClickedSubject = Information(type, subjectName)

        val imgs = MainActivity.photoManager.getSubjectImgs(type, subjectName)

        if(currentDialog != null) currentDialog?.cancel()

        currentDialog = ImgViewDialog(mainActivity, imgs, Information(type, subjectName)).apply {
            setCanceledOnTouchOutside(true)
            setCancelable(true)
            setOnReloadMethod {
                MainActivity.photoManager.getSubjectImgs(type, subjectName)
            }

            show()
        }

    }

    fun reloadDialog() {
        currentDialog?.let { dialog ->
            currentClickedSubject?.let { information ->
                val imgs = MainActivity.photoManager.getSubjectImgs(information.type, information.subjectName)
                dialog.changeImages(imgs)
            }
        }
    }
}