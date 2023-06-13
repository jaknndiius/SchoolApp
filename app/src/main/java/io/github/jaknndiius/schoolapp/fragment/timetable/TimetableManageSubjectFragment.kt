package io.github.jaknndiius.schoolapp.fragment.timetable

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipDescription
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.LinearLayout.FIND_VIEWS_WITH_CONTENT_DESCRIPTION
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.database.Subject
import io.github.jaknndiius.schoolapp.fragment.TimetableFragment
import kotlinx.coroutines.*

class TimetableManageSubjectFragment(
    private val parent: TimetableFragment
) : Fragment() {

    private var isSelectMod = false

    lateinit var binding: View
    lateinit var inflater: LayoutInflater

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflate(R.layout.fragment_timetable_setting_manage_subject, container, false)
        this.inflater = inflater
        binding.setOnClickListener {
            leaveSelectMod()
        }
        setToBackButton()
        setButtonToGenerate()

        reloadSubjects(false)

        return binding
    }

    private fun reloadSubjects(animation: Boolean) {
        val subjectLayout: LinearLayout = binding.findViewById(R.id.subject_layout)
        subjectLayout.removeAllViews()
        CoroutineScope(Dispatchers.IO).launch {
            val subjects = MainActivity.subjectManager.getAll()
            withContext(Dispatchers.Main) {
                var delay = 0L
                subjects.forEach {
                    val view = makeSubject(subjectLayout, it)
                    if(animation) {
                        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in).apply {
                            startOffset = delay
                            delay += 50 })}
                    subjectLayout.addView(view)
                }
                if(animation) {
                binding.findViewById<LinearLayout>(R.id.generate_subject).startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in).apply {
                    startOffset = delay
                })}
            }
        }
    }

    private fun makeSubject(container: ViewGroup, subject: Subject): View {
        return inflater.inflate(R.layout.fragment_manage_subject, container, false).apply {
            contentDescription = "SubjectView"
            findViewById<TextView>(R.id.subject_name).text = subject.name
            findViewById<TextView>(R.id.teacher_name).text = subject.teacherName?: "-"
            setOnLongClickListener {
                startSelectMod()
                false
            }
            setOnClickListener {
                if(isSelectMod) {
                    val checkBox: CheckBox = findViewById(R.id.select_checkbox)
                    checkBox.isChecked = !checkBox.isChecked
                }
            }
        }
    }

    private fun getAllSubjectViews(): List<View> {
        val outputViews: ArrayList<View> = arrayListOf()
        binding.findViewsWithText(outputViews, "SubjectView", FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
        return outputViews
    }

    private fun getAllCheckedSubjectViews(): List<View> {
        return getAllSubjectViews().filter { it.findViewById<CheckBox>(R.id.select_checkbox).isChecked }
    }

    private fun deleteCheckedSubjects() {
        CoroutineScope(Dispatchers.IO).launch {
            var sum = 0
            getAllCheckedSubjectViews().forEach {
                MainActivity.subjectManager.delete(it.findViewById<TextView>(R.id.subject_name).text.toString())
                sum += 1
            }
            withContext(Dispatchers.Main) {
                reloadSubjects(true)
                leaveSelectMod()
                Toast.makeText(context, "과목 ${sum}개를 삭제하였습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deleteSubject() {
        for(view in getAllCheckedSubjectViews()) {
            AlertDialog.Builder(context)
                .setMessage("체크한 모든 과목을 삭제하시겠습니까?")
                .setPositiveButton("네", DialogInterface.OnClickListener { _, _ -> deleteCheckedSubjects() })
                .setNegativeButton("아니요", null)
                .show()
            break
        }
    }

    private fun setToBackButton() {
        binding.findViewById<TextView>(R.id.title).apply {
            text = resources.getString(R.string.setting_manage_subject)
            startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
        }
        binding.findViewById<Button>(R.id.back_button).setOnClickListener {
            parent.openTimetableSetting(TimetableFragment.Direction.PREVIOUS)
        }
    }
    private fun setToLeavingButton() {
        binding.findViewById<TextView>(R.id.title).apply {
            text = resources.getString(R.string.select)
            startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
        }
        binding.findViewById<Button>(R.id.back_button).setOnClickListener {
            leaveSelectMod()
        }
    }

    private fun setButtonToDelete() {
        binding.findViewById<LinearLayout>(R.id.generate_subject).apply {
            background = resources.getDrawable(R.drawable.time_table_subject_background_changed)
            findViewById<AppCompatButton>(R.id.subject_merge_button).apply {
                setTextColor(resources.getColor(R.color.red))
                setText(R.string.delete_subject)
                setOnClickListener {
                    deleteSubject()
                }
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
            }
        }
    }

    private fun setButtonToGenerate() {
        binding.findViewById<LinearLayout>(R.id.generate_subject).apply {
            background = resources.getDrawable(R.drawable.time_table_subject_background)
            findViewById<AppCompatButton>(R.id.subject_merge_button).apply {
                setTextColor(resources.getColor(R.color.green))
                setText(R.string.generate_subject)
                setOnClickListener {
                    this@TimetableManageSubjectFragment.parent.openSubjectGenerator(TimetableFragment.Direction.NEXT)
                }
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
            }
        }
    }

    private fun startSelectMod() {
        if(isSelectMod) return
        isSelectMod = true

        setToLeavingButton()
        setButtonToDelete()

        getAllSubjectViews().forEach {
            ObjectAnimator.ofFloat(it.findViewById(R.id.subject_name), "translationX", 100f).apply {
                duration = 200
                start()
            }
            it.findViewById<CheckBox>(R.id.select_checkbox).apply {
                isChecked = false
                visibility = View.VISIBLE

                startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_with_term))
            }
        }
    }

    private fun leaveSelectMod() {
        if(!isSelectMod) return
        isSelectMod = false

        setToBackButton()
        setButtonToGenerate()

        getAllSubjectViews().forEach {
            ObjectAnimator.ofFloat(it.findViewById(R.id.subject_name), "translationX", 0f).apply {
                startDelay = 200
                duration = 200
                start()
            }
            it.findViewById<CheckBox>(R.id.select_checkbox).apply {
                visibility = View.GONE
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
            }
        }
    }
}