package io.github.jaknndiius.schoolapp.fragment.timetable

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
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

class ManageSubjectFragment(
    private val timetableFragment: TimetableFragment
) : Fragment() {

    private var isSelectMod = false

    lateinit var binding: View
    lateinit var inflater: LayoutInflater
    lateinit var editButton: EditButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflate(R.layout.timetable_setting_manage_subject, container, false)
        this.inflater = inflater
        editButton = EditButton(binding.findViewById(R.id.edit_subject_layout))

        binding.setOnClickListener {
            leaveSelectMod()
        }
        setToBackButton()

        reloadSubjects(false)

        return binding
    }

    private fun reloadSubjects(loadAnimation: Boolean) {
        val subjectLayout: LinearLayout = binding.findViewById(R.id.subject_layout)
        subjectLayout.removeAllViews()
        CoroutineScope(Dispatchers.IO).launch {
            val subjects = MainActivity.subjectManager.getAll()
            withContext(Dispatchers.Main) {
                var delay = 0L
                subjects.forEach {
                    val view = makeSubjectView(subjectLayout, it)
                    if(loadAnimation) {
                        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in).apply {
                            startOffset = delay
                            delay += 50 })}
                    subjectLayout.addView(view)
                }
                if(loadAnimation) editButton.fadeIn(delay)
            }
        }
    }

    private fun makeSubjectView(container: ViewGroup, subject: Subject): View {
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
                    findViewById<CheckBox?>(R.id.select_checkbox).apply {
                        isChecked = !isChecked
                    }
                    editButton.reloadDeleteButton()
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
            val views = getAllCheckedSubjectViews()
            views.forEach {
                MainActivity.subjectManager.delete(it.findViewById<TextView>(R.id.subject_name).text.toString())
            }
            withContext(Dispatchers.Main) {
                reloadSubjects(true)
                leaveSelectMod()
                Toast.makeText(context, getString(R.string.say_deleted_subjects_with_count, views.size), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun alertDelete() {
        AlertDialog.Builder(context)
            .setMessage(getString(R.string.ask_really_delete))
            .setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { _, _ -> deleteCheckedSubjects() })
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun setToBackButton() {
        binding.findViewById<TextView>(R.id.title).apply {
            text = getString(R.string.setting_manage_subject)
            startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
        }
        binding.findViewById<Button>(R.id.back_button).setOnClickListener {
            timetableFragment.openTimetableSetting(TimetableFragment.Direction.PREVIOUS)
        }
    }
    private fun setToLeavingButton() {
        binding.findViewById<TextView>(R.id.title).apply {
            text = getString(R.string.select)
            startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
        }
        binding.findViewById<Button>(R.id.back_button).setOnClickListener {
            leaveSelectMod()
        }
    }

    private fun startSelectMod() {
        if(isSelectMod) return
        isSelectMod = true

        setToLeavingButton()
        editButton.setModeToDelete()

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
        editButton.setModeToGenerate()

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

    private enum class ButtonMod {DELETE, GENERATE}
    inner class EditButton( private val buttonLayout: LinearLayout ) {

        private val button: AppCompatButton = buttonLayout.findViewById(R.id.edit_subject_button)
        private var buttonMode = ButtonMod.GENERATE
        private var disabled = false

        init {
            setModeToGenerate()
        }

        private fun mergeButton(buttonBackground: Int, textColor: Int, buttonText: String) {
            buttonLayout.apply {
                background = resources.getDrawable(buttonBackground)
            }
            button.apply {
                text = buttonText
                setTextColor(resources.getColor(textColor))
            }
        }

        fun fadeIn(startOffset: Long) {
            buttonLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in).apply {
                this.startOffset = startOffset
            })
        }

        private fun enableDeleteButton(checkedCount: Int) {
            disabled = false
            mergeButton(R.drawable.time_table_subject_button_background_enabled, R.color.red, resources.getString(R.string.delete_subject_with_count, checkedCount))
            buttonLayout.translationZ = 10F
        }

        private fun disableDeleteButton() {
            disabled = true
            mergeButton(R.drawable.time_table_subject_button_background_disabled, R.color.gray, resources.getString(R.string.delete_subject))
            buttonLayout.translationZ = 0F
        }

        fun reloadDeleteButton() {
            if(buttonMode != ButtonMod.DELETE) return
            val size = getAllCheckedSubjectViews().size
            if(size == 0) disableDeleteButton()
            else enableDeleteButton(size)
        }

        fun setModeToGenerate() {
            buttonMode = ButtonMod.GENERATE
            mergeButton(R.drawable.time_table_subject_background, R.color.green, resources.getString(R.string.generate_subject))
            buttonLayout.translationZ = 10F
            button.apply {
                setOnClickListener {
                    timetableFragment.openSubjectGenerator(TimetableFragment.Direction.NEXT)
                }
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
            }
        }

        fun setModeToDelete() {
            buttonMode = ButtonMod.DELETE
            enableDeleteButton(1)
            button.apply {
                setOnClickListener {
                    if(!disabled) alertDelete()
                }
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
            }
        }
    }
}