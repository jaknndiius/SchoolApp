package io.github.jaknndiius.schoolapp.fragment.timetable.list

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.camera.data.Information
import io.github.jaknndiius.schoolapp.dialog.ImgViewDialog
import io.github.jaknndiius.schoolapp.dialog.PhotoTypeDialog
import io.github.jaknndiius.schoolapp.preset.InformationType
import io.github.jaknndiius.schoolapp.view.ListViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PhotoFragment: Fragment() {

    lateinit var binding: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflater.inflate(R.layout.timetable_list_photo, container, false)
        val listView: ListView = binding.findViewById(R.id.folder_list)

        val adapter = PhotoListAdapter()

        CoroutineScope(Dispatchers.IO).launch {
            val subjects = MainActivity.subjectManager.getAll()

            withContext(Dispatchers.Main) {
                for(subject in subjects) {
                    adapter.addItem(subject.name)
                }
                listView.adapter = adapter
            }
        }

        return binding
    }

    inner class PhotoListAdapter: ListViewAdapter<String>() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val subjectName = getItem(position)

            val view = LayoutInflater.from(context).inflate(R.layout.timetable_list_photo_folder, parent, false)
            view.findViewById<TextView>(R.id.subject_name).text = subjectName
            view.findViewById<ConstraintLayout>(R.id.background).setOnClickListener {
                askTypeAndRun { index ->
                    openDialog(subjectName,
                        when (index) {
                            0 -> InformationType.PERFORMANCE
                            1 -> InformationType.EXAM
                            else -> InformationType.PERFORMANCE
                        }
                    )
                }
            }
            view.findViewById<AppCompatButton>(R.id.take_photo_button).setOnClickListener {
                askTypeAndRun { index ->
                    MainActivity.photoManager.dispatchTakePictureIntent(
                        Information(
                            when (index) {
                                0 -> InformationType.PERFORMANCE
                                1 -> InformationType.EXAM
                                else -> InformationType.PERFORMANCE
                            },
                            subjectName,
                            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                        )
                    )
                }
            }

            return view
        }
    }

    private fun askTypeAndRun(run: (index: Int) -> Unit) {
        PhotoTypeDialog(context, run).show()
    }

    fun openDialog(subjectName: String, informationType: InformationType) {

        val loading = ProgressDialog(activity as MainActivity).apply {
            setMessage("사진을 로딩중입니다.")
            setCancelable(false)
            setProgressStyle(androidx.appcompat.R.style.Widget_AppCompat_ProgressBar_Horizontal)
            show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val pics = MainActivity.photoManager.getSubjectImgs(informationType, subjectName)

            withContext(Dispatchers.Main) {
                loading.dismiss()

                if(pics.isEmpty()) {
                    Toast.makeText(context, getString(R.string.say_no_pictures), Toast.LENGTH_LONG).show()
                    return@withContext
                }

                ImgViewDialog(activity as MainActivity, subjectName, pics) {
                    MainActivity.photoManager.getSubjectImgs(informationType, subjectName)
                }.show()
            }
        }
    }

}