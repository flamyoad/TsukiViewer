package com.flamyoad.tsukiviewer

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.flamyoad.tsukiviewer.ui.home.collection.CollectionDoujinFragment
import com.flamyoad.tsukiviewer.ui.home.local.LocalDoujinsFragment
import com.flamyoad.tsukiviewer.ui.home.tags.DoujinTagsFragment
import com.flamyoad.tsukiviewer.ui.settings.SettingsActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        Log.d("localdoujins", "MainActivity's onCreate() is called")

        if (savedInstanceState == null) {
            addFragment(LocalDoujinsFragment.newInstance(), LocalDoujinsFragment.APPBAR_TITLE)
            Log.d("localdoujins", "addFragment(LocalDoujinsFragment.newInstance())")
        }
    }

    override fun onResume() {
        super.onResume()
        val fragmentTag = supportFragmentManager.findFragmentById(R.id.container)?.tag ?: ""

        if (fragmentTag == DoujinTagsFragment.APPBAR_TITLE) {
            removeAppBarShadow()
        } else {
            showAppBarShadow()
        }

        setTitle(fragmentTag)
    }

    // Note to self: The onOptionsItemSelected() event actually bubbles up from Activity to Fragment
    // I thought it was the other way round.
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_search_local -> {
                /*  There is no need to animate the closing of DrawerLayout if we start a new activity.
                    This is because the animation has to be completed before the new activity can be started.
                    It can cause delay and is bad for UX
                 */
                drawerLayout.closeDrawer(GravityCompat.START, false)
                return false
            }
            else -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                return false
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val currentFragmentTag = supportFragmentManager.findFragmentById(R.id.container)?.tag ?: ""

        when (item.itemId) {
            R.id.nav_localdoujins -> {
                if (currentFragmentTag != LocalDoujinsFragment.APPBAR_TITLE) {
                    clearFragmentBackStack()

                    item.isChecked = true
                    setTitle(LocalDoujinsFragment.APPBAR_TITLE)

                    showAppBarShadow()
                }
            }

            R.id.nav_tags -> {
                if (currentFragmentTag != DoujinTagsFragment.APPBAR_TITLE) {
                    val fragment = DoujinTagsFragment.newInstance()
                    pushFragment(fragment, fragment.getTitle())
                    item.isChecked = true

                    removeAppBarShadow()
                }
            }

            R.id.nav_favourites -> {
                if (currentFragmentTag != CollectionDoujinFragment.APPBAR_TITLE) {
                    val fragment = CollectionDoujinFragment.newInstance()
                    pushFragment(fragment, fragment.getTitle())
                    item.isChecked = true

                    showAppBarShadow()
                }
            }

            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)

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
        appbarLayout.elevation = 0f
    }

    private fun showAppBarShadow() {
        appbarLayout.elevation = 8f
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            clearFragmentBackStack()
            setTitle(LocalDoujinsFragment.APPBAR_TITLE)
            nav_view.setCheckedItem(R.id.nav_localdoujins)

        } else {
            showQuitAppDialog()
        }
    }

    private fun showQuitAppDialog() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Quit")
            setMessage("Do you want to close this application?")
            setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                finish() // Quit the application
            })
            setNegativeButton("Return", DialogInterface.OnClickListener { dialog, which ->
                // do nothing
            })
        }
        builder.show()
    }
}
