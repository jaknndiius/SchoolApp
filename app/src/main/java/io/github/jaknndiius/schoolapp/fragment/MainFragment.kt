package io.github.jaknndiius.schoolapp.fragment

import io.github.jaknndiius.schoolapp.enum.Direction

interface MainFragment {
    fun changeHeader(offset: Float, from: Direction)
}