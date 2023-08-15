package io.github.jaknndiius.schoolapp.animation

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import io.github.jaknndiius.schoolapp.R

object CustomAnimation {
    fun scaleUp(context: Context, view: View) {
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up))
    }
    fun scaleDown(context: Context, view: View) {
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_down))
    }
}