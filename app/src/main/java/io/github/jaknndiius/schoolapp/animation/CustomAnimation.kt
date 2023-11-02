package io.github.jaknndiius.schoolapp.animation

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
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

    fun changeColor(context: Context, view: View, from: Int, to: Int) {

        ValueAnimator.ofObject(ArgbEvaluator(), context.resources.getColor(from), context.resources.getColor(to)).apply {
            duration = 100
            addUpdateListener {
                view.backgroundTintList = ColorStateList.valueOf(it.animatedValue as Int)
            }
            start()
        }
    }
}