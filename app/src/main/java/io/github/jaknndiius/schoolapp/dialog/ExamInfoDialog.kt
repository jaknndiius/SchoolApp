package io.github.jaknndiius.schoolapp.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.database.Subject
import io.github.jaknndiius.schoolapp.preset.RangeType

class ExamInfoDialog(
    context: Context,
    private val subject: Subject
): AlertDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.exam_info_dialog_layout)
        window?.setBackgroundDrawableResource(R.drawable.transparent)

        val inflater: LayoutInflater = LayoutInflater.from(context)

        findViewById<TextView>(R.id.subject_name).text = context.getString(R.string.exam_info_with_subject, subject.name)

        val rangeInfoLayout: LinearLayout = findViewById(R.id.range_infos)
        subject.examAttr?.let { examAttr ->
            examAttr.ranges.forEach {
                rangeInfoLayout.addView(makeRange(inflater, rangeInfoLayout, it))
            }

            examAttr.questionsCount?.let { counts ->
                findViewById<TextView>(R.id.gakgwan).text = context.getString(R.string.gakgwan_with_count, counts[0])
                findViewById<TextView>(R.id.sursul).text = context.getString(R.string.sursul_with_count, counts[1])
            }

        }
    }

    private fun makeRange(inflater: LayoutInflater, container: ViewGroup, range: Pair<RangeType, String>): View {
        val rangeLayout = inflater.inflate(R.layout.range_text_layout, container, false)
        rangeLayout.findViewById<TextView>(R.id.range_info).text = range.second

        range.first.let { type ->
            rangeLayout.findViewById<ImageView>(R.id.icon).setBackgroundResource(type.drawableId)
            rangeLayout.findViewById<TextView>(R.id.type).text = type.korean
        }

        return rangeLayout
    }
}