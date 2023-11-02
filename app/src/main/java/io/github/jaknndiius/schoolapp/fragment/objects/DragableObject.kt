package io.github.jaknndiius.schoolapp.fragment.timetable

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isEmpty
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.animation.CustomAnimation

class DraggableObject(
     private val context: Context?,
     private val inflater: LayoutInflater,
     private val binding: View,
     private val tableLayout: LinearLayout
) {

     private val blank: View = makeSubject(tableLayout, "+").apply {
         setOnDragListener { v, e ->
             when (e.action) {
                 DragEvent.ACTION_DRAG_STARTED -> e.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                 DragEvent.ACTION_DRAG_ENTERED -> {
                     CustomAnimation.scaleDown(context, v.findViewById<LinearLayout>(R.id.subject_background))
                     true
                 }
                 DragEvent.ACTION_DRAG_LOCATION -> true
                 DragEvent.ACTION_DROP -> {
                     appendSubjectToTableAt(e.clipData.getItemAt(0).text.toString(), tableLayout.indexOfChild(v))
                     true
                 }
                 DragEvent.ACTION_DRAG_EXITED -> {
                     CustomAnimation.scaleUp(context, v.findViewById<LinearLayout>(R.id.subject_background))
                     true
                 }
                 DragEvent.ACTION_DRAG_ENDED -> true
                 else -> false
             }
         }
     }

    private val currentSetting = object {
        private var settingList = arrayListOf<String>()
        fun reset(elements: List<String>) {
            settingList = ArrayList(elements)
        }
        fun insert(index: Int, element: String) {
            settingList.add(index, element)
        }
        fun removeAt(index: Int) {
            settingList.removeAt(index)
        }
        fun get(): List<String> {
            return settingList
        }
    }

    var onReload: () -> Unit = {}

    init {
        tableLayout.setOnDragListener{ _, e ->
            when (e.action) {
                DragEvent.ACTION_DRAG_STARTED -> e.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                DragEvent.ACTION_DRAG_ENTERED -> true
                DragEvent.ACTION_DRAG_LOCATION -> true
                DragEvent.ACTION_DRAG_EXITED -> true
                DragEvent.ACTION_DROP -> true
                DragEvent.ACTION_DRAG_ENDED -> {
                    if(!e.result) Toast.makeText(binding.context, context?.getString(R.string.request_right_drag), Toast.LENGTH_SHORT).show()
                    reloadTable()
                    true
                }
                else -> false
            }
        }
    }

    fun setOnReloadMethod(onReload: () -> Unit) {
        this.onReload = onReload
    }

    fun getSettings(): List<String> {
        return currentSetting.get()
    }

    private fun addToTable(subjectNames: List<String>) {
        if(subjectNames.isEmpty()) {
            tableLayout.addView(blank)
        } else {
            subjectNames.forEach {
                tableLayout.addView(makeTableSubject(it))
            }
        }

    }

    private fun reloadTable() {
        clearTable()
        addToTable(getSettings())
        onReload()
    }

    fun reloadTableWithObjects(titles: List<String>) {
        clearTable()
        currentSetting.reset(titles)
        addToTable(titles)
        onReload()
    }

    fun clearTable() {
        tableLayout.removeAllViews()
    }

    private fun appendSubjectToTableAt(subjectName: String, index: Int) {
        currentSetting.insert(index, subjectName)
        reloadTable()
    }

    private fun removeSubjectAt(index: Int) {
        currentSetting.removeAt(index)
        reloadTable()
    }

    private fun makeSubject(container: ViewGroup, subjectName: String): View {
        return inflater.inflate(R.layout.timetable_drop_subject_layout, container, false).apply {
            findViewById<TextView>(R.id.subject_name).text = subjectName
        }
    }

     fun makeDraggableSubject(container: ViewGroup, subjectName: String): View {
        return makeSubject(container, subjectName).apply {
            findViewById<TextView>(R.id.subject_name).text = subjectName
            tag = subjectName
            setOnLongClickListener { v ->
                val item = ClipData.Item(v.tag as? CharSequence)
                val dragData = ClipData(
                    v.tag as? CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item
                )
                val shadow = SubjectDragShadowBuilder(this)
                v.startDragAndDrop(
                    dragData,
                    shadow,
                    null,
                    0
                )
                true
            }
        }
    }

    private fun insertBlank(index: Int = 0) {
        tableLayout.removeView(blank)
        tableLayout.addView(blank, index)
    }

     private fun makeTableSubject(subjectName: String): View {
        return makeSubject(tableLayout, subjectName).apply {
            tag = subjectName
            setOnLongClickListener { v ->
                val item = ClipData.Item(v.tag as? CharSequence)
                val dragData = ClipData(
                    v.tag as? CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item
                )
                val shadow = SubjectDragShadowBuilder(this)
                v.startDragAndDrop(
                    dragData,
                    shadow,
                    null,
                    0
                )
                removeSubjectAt(tableLayout.indexOfChild(v))
                true
            }
            setOnDragListener { v, e ->
                when (e.action) {
                    DragEvent.ACTION_DRAG_STARTED -> e.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    DragEvent.ACTION_DRAG_ENTERED -> {
                        CustomAnimation.scaleDown(context, v.findViewById<LinearLayout>(R.id.subject_background))
                        true
                    }
                    DragEvent.ACTION_DRAG_LOCATION -> {
                        tableLayout.indexOfChild(v).let {
                            insertBlank(if (e.y <= v.height / 2) it else (it + 1))
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            binding.findViewById<ScrollView>(R.id.scroll_subjects).scrollToDescendant(blank)
                        }
                        true
                    }
                    DragEvent.ACTION_DRAG_EXITED -> {
                        CustomAnimation.scaleUp(context, v.findViewById<LinearLayout>(R.id.subject_background))
                        true
                    }
                    DragEvent.ACTION_DROP -> {
                        appendSubjectToTableAt(e.clipData.getItemAt(0).text.toString(), tableLayout.indexOfChild(v))
                        true
                    }
                    DragEvent.ACTION_DRAG_ENDED -> true
                    else -> false
                }
            }
        }
    }

     class SubjectDragShadowBuilder(view: View) : View.DragShadowBuilder(view) {

        private val shadow = ColorDrawable(Color.LTGRAY)

        override fun onProvideShadowMetrics(size: Point, touch: Point) {
            val width: Int = view.width
            val height: Int = view.height
            shadow.setBounds(0, 0, width, height)
            size.set(width, height)
            touch.set(width / 2, height / 2)
        }

        override fun onDrawShadow(canvas: Canvas) {
            shadow.draw(canvas)
        }
    }
}