package io.github.jaknndiius.schoolapp.camera.data

import io.github.jaknndiius.schoolapp.preset.InformationType

data class Information(
    val type: InformationType,
    val subjectName: String,
    var date: String? = null
) {
    override fun toString(): String {
        return "${type.name}_${subjectName}"
    }
}