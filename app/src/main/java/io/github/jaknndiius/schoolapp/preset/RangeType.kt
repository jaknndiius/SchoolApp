package io.github.jaknndiius.schoolapp.preset

import io.github.jaknndiius.schoolapp.R

enum class RangeType(
    val korean: String,
    val drawableId: Int
) {
    TEXTBOOK("교과서", R.drawable.ic_textbook),
    SUBBOOK("부교재", R.drawable.ic_subbook),
    PAPER("학습지", R.drawable.ic_paper),
    MOCK("모고", R.drawable.ic_pencil),
    OTHER("그 외", R.drawable.ic_other_types)
}