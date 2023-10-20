package io.github.jaknndiius.schoolapp.fragment

import io.github.jaknndiius.schoolapp.preset.Direction

interface MainFragment {
    fun changeHeader(offset: Float, from: Direction)
}