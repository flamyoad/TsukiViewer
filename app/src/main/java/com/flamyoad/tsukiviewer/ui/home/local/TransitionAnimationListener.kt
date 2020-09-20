package com.flamyoad.tsukiviewer.ui.home.local

import android.view.View
import androidx.core.app.ActivityOptionsCompat

interface TransitionAnimationListener {
    fun makeSceneTransitionAnimation(view: View): ActivityOptionsCompat
}