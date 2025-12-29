package com.flamyoad.tsukiviewer

import android.animation.AnimatorInflater
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.flamyoad.tsukiviewer.databinding.ActivityMainBinding
import com.flamyoad.tsukiviewer.ui.home.bookmarks.BookmarkFragment
import com.flamyoad.tsukiviewer.ui.home.collections.CollectionFragment
import com.flamyoad.tsukiviewer.ui.home.local.LocalDoujinsFragment
import com.flamyoad.tsukiviewer.ui.home.tags.DoujinTagsFragment
import com.flamyoad.tsukiviewer.ui.settings.SettingsActivity
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    private var callback: ActionMode.Callback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        binding.drawerLayout.drawerElevation = 0f
        binding.drawerLayout.setScrimColor(ContextCompat.getColor(this, R.color.navDrawerScrim))

        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            addFragment(LocalDoujinsFragment.newInstance(), LocalDoujinsFragment.APPBAR_TITLE)
        }
    }

    override fun onResume() {
        super.onResume()
        val fragmentTag = supportFragmentManager.findFragmentById(R.id.container)?.tag ?: ""

        when (fragmentTag) {
            DoujinTagsFragment.APPBAR_TITLE -> removeAppBarShadow()
            BookmarkFragment.APPBAR_TITLE -> removeAppBarShadow()
            else -> showAppBarShadow()
        }

        setTitle(fragmentTag)
    }

    // Note to self: onOptionsItemSelected() event actually bubbles up from Activity to Fragment.
    //               So we have to return false here to allow menu processing to proceed.
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_search_local -> {
                /*  There is no need to animate the closing of DrawerLayout if we start a new activity.
                    This is because the animation has to be completed before the new activity can be started.
                    It can cause delay and is bad for UX

                    Activity starting code is in LocalDoujinFragment.kt
                 */
                binding.drawerLayout.closeDrawer(GravityCompat.START, false)
                return false
            }

            else -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return false
            }
        }
    }

    override fun onSupportActionModeFinished(mode: ActionMode) {
        super.onSupportActionModeFinished(mode)
        mode.finish()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.container) as BaseFragment?
        currentFragment?.destroyActionMode()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.container) as BaseFragment?
        val currentFragmentTag = currentFragment?.tag ?: ""

        when (item.itemId) {
            R.id.nav_localdoujins -> {
                if (currentFragmentTag != LocalDoujinsFragment.APPBAR_TITLE) {
                    currentFragment?.destroyActionMode()

                    clearFragmentBackStack()
                    item.isChecked = true
                    setTitle(LocalDoujinsFragment.APPBAR_TITLE)
                    showAppBarShadow()
                }
            }

            R.id.nav_collection -> {
                if (currentFragmentTag != CollectionFragment.APPBAR_TITLE) {
                    currentFragment?.destroyActionMode()
                    val fragment = CollectionFragment.newInstance()
                    pushFragment(fragment, fragment.getTitle())
                    item.isChecked = true
                    showAppBarShadow()
                }
            }

            R.id.nav_tags -> {
                if (currentFragmentTag != DoujinTagsFragment.APPBAR_TITLE) {
                    currentFragment?.destroyActionMode()
                    val fragment = DoujinTagsFragment.newInstance()
                    pushFragment(fragment, fragment.getTitle())
                    item.isChecked = true
                    removeAppBarShadow()
                }
            }

            R.id.nav_bookmark -> {
                if (currentFragmentTag != BookmarkFragment.APPBAR_TITLE) {
                    currentFragment?.destroyActionMode()
                    val fragment = BookmarkFragment.newInstance()
                    pushFragment(fragment, fragment.getTitle())
                    item.isChecked = true
                    removeAppBarShadow()
                }
            }

            R.id.nav_settings -> {
                currentFragment?.destroyActionMode()
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun pushFragment(fragment: Fragment, tagName: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, tagName)
            .addToBackStack("stack")
            .commit()

        setTitle(tagName)
    }

    // Adds fragment without pushing to backstack
    private fun addFragment(fragment: Fragment, tagName: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, tagName)
            .commit()

        setTitle(tagName)
    }

    private fun clearFragmentBackStack() {
        val first = supportFragmentManager.getBackStackEntryAt(0)
        supportFragmentManager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun removeAppBarShadow() {
        binding.appbarLayout.stateListAnimator = AnimatorInflater.loadStateListAnimator(this, R.animator.appbar_elevation_off)
    }

    private fun showAppBarShadow() {
        binding.appbarLayout.stateListAnimator = AnimatorInflater.loadStateListAnimator(this, R.animator.appbar_elevation_on)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return
        }

        if (supportFragmentManager.backStackEntryCount > 0) {
            clearFragmentBackStack()
            setTitle(LocalDoujinsFragment.APPBAR_TITLE)
            binding.navView.setCheckedItem(R.id.nav_localdoujins)

        } else {
            if (MyAppPreference.getInstance(this).askBeforeQuit()) {
                showQuitAppDialog()
            } else {
                finish()
            }
        }
    }

    // Todo: Consider replacing with DialogFragment
    private fun showQuitAppDialog() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Quit")
            setMessage("Do you want to close this application?")
            setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                finish() // Quits the application
            })
            setNegativeButton("Return", DialogInterface.OnClickListener { dialog, which ->
                // do nothing
            })
        }
        builder.show()
    }
}
