package io.github.jaknndiius.schoolapp.fragment

import io.github.jaknndiius.schoolapp.enums.Direction

interface MainFragment {
    fun changeHeader(offset: Float, from: Direction)
}