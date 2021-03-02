package com.flamyoad.tsukiviewer.utils.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
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
