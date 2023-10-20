package io.github.jaknndiius.schoolapp.dialog

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.camera.data.SavedImage
import io.github.jaknndiius.schoolapp.preset.InformationType

class ImgViewDialog(
    private val context: MainActivity,
    private val subjectName: String,
    private val imgs: List<SavedImage>,
    private val reloadImages: () -> List<SavedImage>
): Dialog(context) {

    init {
        setCancelable(true)
        setCanceledOnTouchOutside(true)
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
        changeImages(reloadImages())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.img_view_dialog_layout)
        findViewById<TextView>(R.id.title).text = subjectName
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
            v.findViewById<ImageView>(R.id.undericon).setBackgroundResource(
                when(savedImage.information.type) {
                    InformationType.PERFORMANCE -> R.drawable.ic_clock
                    InformationType.EXAM -> R.drawable.ic_pencil
                }
            )

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

        while (last++ <= 2 ) {
            val currentLayout0 = bitmapLayouts[last%3]
            val v0 = layoutInflater.inflate(R.layout.img_view_element, currentLayout0, false)
            v0.setBackgroundColor(context.resources.getColor(R.color.white))
            v0.findViewById<View>(R.id.undericon).setBackgroundResource(R.drawable.transparent)
            currentLayout0.addView(v0)
        }
    }

    private fun getOptions(): BitmapFactory.Options {
        return BitmapFactory.Options().apply {
            inSampleSize = 4
        }
    }
}