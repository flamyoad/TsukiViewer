package com.flamyoad.tsukiviewer.utils.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar

val imageExtensions = arrayOf("jpg", "png", "gif", "jpeg", "webp", "jpe", "bmp")

fun Context.toast(message: String, lengthLong: Boolean = false) {
    val length = when (lengthLong) {
        true -> Toast.LENGTH_LONG
        false -> Toast.LENGTH_SHORT
    }
    Toast.makeText(this, message, length)
        .show()
}

fun Fragment.toast(message: String, lengthLong: Boolean = false) {
    val length = when (lengthLong) {
        true -> Toast.LENGTH_LONG
        false -> Toast.LENGTH_SHORT
    }
    Toast.makeText(requireContext(), message, length)
        .show()
}

fun View.snackbar(message: String, lengthLong: Boolean = false) {
    val length = when (lengthLong) {
        true -> Snackbar.LENGTH_LONG
        false -> Snackbar.LENGTH_SHORT
    }
    Snackbar.make(this, message, length)
}

fun Activity.snackbar(message: String, lengthLong: Boolean = false) {
    val length = when (lengthLong) {
        true -> Snackbar.LENGTH_LONG
        false -> Snackbar.LENGTH_SHORT
    }
    Snackbar.make(findViewById(android.R.id.content), message, length)
}

fun Activity.snackbar(view: View, message: String, lengthLong: Boolean = false) {
    val length = when (lengthLong) {
        true -> Snackbar.LENGTH_LONG
        false -> Snackbar.LENGTH_SHORT
    }
    Snackbar.make(view, message, length)
}

fun Fragment.snackbar(message: String, lengthLong: Boolean = false) {
    view?.let {
        val length = when (lengthLong) {
            true -> Snackbar.LENGTH_LONG
            false -> Snackbar.LENGTH_SHORT
        }
        Snackbar.make(it.findViewById(android.R.id.content), message, length)
    }
}

/**
 * Reduces drag sensitivity of [ViewPager2] widget
 */
fun ViewPager2.reduceDragSensitivity() {
    try {
        val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true
        val recyclerView = recyclerViewField.get(this) as RecyclerView

        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true
        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop * 5)
    } catch (ignored: Exception) {
    }
}

/**
 * Slightly reduces drag sensitivity of [ViewPager2] widget
 */
fun ViewPager2.reduceDragSensitivitySlightly() {
    try {
        val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true
        val recyclerView = recyclerViewField.get(this) as RecyclerView

        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true
        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop * 5)
    } catch (ignored: Exception) {
    }
}

