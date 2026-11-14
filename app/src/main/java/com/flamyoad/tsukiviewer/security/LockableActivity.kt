package com.flamyoad.tsukiviewer.security

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.ui.R as UiR

/** Keeps the current activity and its navigation state behind the unlock screen. */
abstract class LockableActivity : AppCompatActivity() {
    internal val appLock get() = (application as MyApplication).appLock

    private lateinit var privacyCover: View

    override fun onCreate(savedInstanceState: Bundle?) {
        updateSecureWindow()
        super.onCreate(savedInstanceState)
        val decor = window.decorView as FrameLayout
        privacyCover = View(this).apply {
            setBackgroundColor(Color.BLACK)
            elevation = Float.MAX_VALUE
            isClickable = true
            isFocusableInTouchMode = true
            isVisible = appLock.enabled.value
        }
        decor.addView(privacyCover, FrameLayout.LayoutParams(-1, -1))
    }

    override fun onStart() {
        super.onStart()
        appLock.activityStarted()
        updateSecureWindow()
        if (appLock.isLocked) coverContent()
    }

    override fun onPostResume() {
        super.onPostResume()
        appLock.activityResumed()
        if (appLock.isLocked) {
            coverContent()
            startActivity(AppLockActivity.unlockIntent(this))
        } else {
            updateSecureWindow()
            privacyCover.isVisible = false
            onAppUnlocked()
        }
    }

    override fun onStop() {
        // Cover the decor before Android can draw this activity again on task restoration.
        if (appLock.enabled.value) coverContent()
        appLock.activityStopped(isChangingConfigurations)
        super.onStop()
    }

    internal fun requestAppLockChange(enabled: Boolean) {
        if (!appLock.isLocked && enabled != appLock.enabled.value) {
            startActivity(AppLockActivity.settingsIntent(this, enabled))
        }
    }

    internal fun showAppLockTimeoutPicker() {
        val options = AppLockTimeout.entries
        AlertDialog.Builder(this)
            .setTitle(UiR.string.app_lock_timeout)
            .setSingleChoiceItems(
                options.map { getText(it.label) }.toTypedArray(),
                options.indexOf(appLock.timeout.value)
            ) { dialog, selection ->
                if (!appLock.isLocked && !appLock.setTimeout(options[selection])) {
                    Toast.makeText(this, R.string.app_lock_save_failed, Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    protected open fun onAppUnlocked() = Unit

    private fun coverContent() {
        privacyCover.bringToFront()
        privacyCover.isVisible = true
        privacyCover.requestFocus()
    }

    private fun updateSecureWindow() {
        if (appLock.enabled.value) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
