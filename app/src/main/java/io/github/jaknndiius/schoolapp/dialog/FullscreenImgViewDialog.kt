package io.github.jaknndiius.schoolapp.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jaknndiius.schoolapp.MainActivity
import io.github.jaknndiius.schoolapp.R
import io.github.jaknndiius.schoolapp.camera.Photo
import io.github.jaknndiius.schoolapp.camera.data.SavedImage
import java.io.File
import java.io.FileOutputStream

class FullscreenImgViewDialog(
    private val context: MainActivity,
    private val img: SavedImage,
    private val parent: ImgViewDialog?
): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.img_view_fullscreen_dialog_layout)

        val view = ImageView(context) //ImageDisplayView(context)
        view.setImageBitmap(Bitmap.createBitmap(img.bitmap, 0, 0, img.bitmap.width, img.bitmap.height,
            Matrix().apply {
                postRotate(90F)
            }, false))
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        view.scaleType = ImageView.ScaleType.FIT_CENTER

        findViewById<LinearLayout>(R.id.img_layout).addView(view, params)

        findViewById<BottomNavigationView>(R.id.bottom_navbar).setOnNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.menu_save -> {
                    val result = saveImageToGallery()
                    Toast.makeText(context,
                    if(result) "사진을 저장했습니다." else "사진 저장에 실패했습니다.",
                    Toast.LENGTH_SHORT).show()
                }
                R.id.menu_share -> shareImage()
                R.id.menu_delete -> deleteImage()
            }
            true

        }
    }

    private fun saveImageToGallery(): Boolean {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {

            val rootPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString()
            val dirName = "/${context.getString(R.string.app_name)}"
            val fileName = System.currentTimeMillis().toString() + ".png"
            val savePath = File(rootPath + dirName)
            savePath.mkdirs()

            val file = File(savePath, fileName)
            if (file.exists()) file.delete()

            try {
                val out = FileOutputStream(file)
                img.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()

                context.sendBroadcast(
                    Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + Environment.getExternalStorageDirectory())
                    )
                )
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    private fun shareImage() {
        val bitmapPath = MediaStore.Images.Media.insertImage(context.contentResolver, img.bitmap, "${img.information}_${img.information.date}", null)
        val bitmapUri = Uri.parse(bitmapPath)
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/*"
        context.startActivity(Intent.createChooser(intent, "이미지 공유하기"))
    }

    private fun deleteImage() {
        val file = File(img.path)

        AlertDialog.Builder(context)
            .setMessage("정말 삭제하시겠습니까?")
            .setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                val result = file.delete()
                if (result) {
                    Toast.makeText(context, "사진을 삭제했습니다.", Toast.LENGTH_SHORT).show()
                    parent?.requestReload()
                    this.cancel()
                } else Toast.makeText(context, "사진 삭제를 실패했습니다.", Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton(context.getString(R.string.no)) { _, _ -> }
            .show()
    }

}