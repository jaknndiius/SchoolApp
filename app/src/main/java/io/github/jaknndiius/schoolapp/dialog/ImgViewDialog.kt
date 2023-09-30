package io.github.jaknndiius.schoolapp.dialog

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.camera.Photo
import io.github.jaknndiius.schoolapp.camera.data.Information
import io.github.jaknndiius.schoolapp.camera.data.SavedImage
import io.github.jaknndiius.schoolapp.enum.InformationType
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.security.auth.Subject

class ImgViewDialog(
    private val context: MainActivity,
    private val imgs: List<SavedImage>,
    private val information: Information
): Dialog(context) {

    private var reloadImages: (() -> List<SavedImage>)? = null

    fun setOnReloadMethod(a: () -> List<SavedImage>) {
        reloadImages = a
    }

    fun changeImages(images: List<SavedImage>) {
        listOf<LinearLayout>(
            findViewById(R.id.layout_line1),
            findViewById(R.id.layout_line2),
            findViewById(R.id.layout_line3)
        ).forEach {
            it.removeAllViews()
        }
        addImages(images)
    }

    fun requestReload() {
        reloadImages?.let { changeImages(it()) }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.img_view_dialog_layout)

        findViewById<TextView>(R.id.title).setText("${information.subjectName}${
            when(information.type) {
                InformationType.PERFORMANCE -> "(수행평가)"
                InformationType.EXAM -> "(시험)"
            }
        }")
        addImages(imgs)

    }

    fun addImages(images: List<SavedImage>) {
        val bitmapLayouts = listOf<LinearLayout>(
            findViewById(R.id.layout_line1),
            findViewById(R.id.layout_line2),
            findViewById(R.id.layout_line3)
        )

        var last = -1
        images.forEachIndexed { index, savedImage ->
            last = index

            val currentLayout = bitmapLayouts[index%3]
            val v = layoutInflater.inflate(R.layout.img_view_element, currentLayout, false)

            v.findViewById<ImageView>(R.id.imgv).apply {

                BitmapFactory.decodeFile(savedImage.path, getOptions())?.also { bitmap ->
                    setImageBitmap(
                        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height,
                            Matrix().apply {
                                postRotate(90F)
                            }, false)
                    )
                }
            }
            v.setOnClickListener {
                val dialog = FullscreenImgViewDialog(context, savedImage, this)
                dialog.setCanceledOnTouchOutside(true)
                dialog.setCancelable(true)
                dialog.show()
            }

            currentLayout.addView(v)
        }

        val currentLayout = bitmapLayouts[(++last)%3]
        val v = layoutInflater.inflate(R.layout.img_view_element, currentLayout, false)
        v.findViewById<ImageView>(R.id.imgv).apply {
            setImageResource(R.drawable.ic_take_photo)
        }
        v.setOnClickListener {
            MainActivity.photoManager.dispatchTakePictureIntent(
                information.apply {
                    date = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                }
            )
        }
        currentLayout.addView(v)

        while (last++ <= 2 ) {
            val currentLayout0 = bitmapLayouts[last%3]
            val v0 = layoutInflater.inflate(R.layout.img_view_element, currentLayout0, false)
            v0.setBackgroundColor(context.resources.getColor(R.color.white))
            currentLayout0.addView(v0)
        }
    }

    private fun getOptions(): BitmapFactory.Options {
        return BitmapFactory.Options().apply {
            inSampleSize = 4
        }
    }
}