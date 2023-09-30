package io.github.jaknndiius.schoolapp.camera.data

import android.graphics.Bitmap

data class SavedImage(
    val information: Information,
    val bitmap: Bitmap,
    val path: String
)