package io.github.jaknndiius.schoolapp.camera

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import io.github.jaknndiius.schoolapp.enum.InformationType
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Photo(
    private val activity: Activity
) {

    companion object {
        val REQUEST_IMAGE_CAPTURE = 131
    }

    private var lastPhotoPath: String? = null

    data class Information(
        val type: InformationType,
        val subjectName: String,
        var date: String? = null
    ) {
        override fun toString(): String {
            return "${type.name}_${subjectName}"
        }
    }

    data class SavedImage(
        val information: Information,
        val bitmap: Bitmap,
        val path: String
    )

    fun getLastBitmap(): Bitmap? {
        BitmapFactory.decodeFile(lastPhotoPath)?.also { bitmap ->
            return bitmap
        }
        return null
    }

    private val imageFormatRegex = "IMAGE_(?<type>EXAM|PERFORMANCE)_(?<subject>[a-z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣|0-9]*)_(?<date>\\w*_\\w*)_\\w*.jpg".toRegex()

    private fun getFromFile(file: File): SavedImage? {
        imageFormatRegex.matchEntire(file.name)?.groups?.let { groups ->
            BitmapFactory.decodeFile(file.absolutePath)?.also { bitmap ->
                return SavedImage(
                    Information(
                        InformationType.valueOf(groups["type"]!!.value),
                        groups["subject"]!!.value,
                        groups["date"]!!.value
                    ), bitmap, file.absolutePath
                )
            }
        }
        return null
    }

    fun getSubjectImgs(type: InformationType, subjectName: String): List<SavedImage> {
        return arrayListOf<SavedImage>().apply {
            activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.listFiles()?.let { files ->
                addAll(
                    files
                        .mapNotNull { file -> getFromFile(file) }
                        .filter { savedImage -> savedImage.information.type == type && savedImage.information.subjectName == subjectName}
                )
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(information: Information): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "IMAGE_${information}_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            lastPhotoPath = absolutePath
        }
    }

    fun dispatchTakePictureIntent(information: Information) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity.packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile(information)
                } catch (_: IOException) { null }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        activity,
                        "io.github.jaknndiius.schoolapp.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }
}