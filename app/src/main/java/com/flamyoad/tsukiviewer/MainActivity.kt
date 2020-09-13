package com.flamyoad.tsukiviewer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.flamyoad.tsukiviewer.ui.home.collection.CollectionDoujinFragment
import com.flamyoad.tsukiviewer.ui.home.local.LocalDoujinsFragment
import com.flamyoad.tsukiviewer.ui.home.online.OnlineDoujinFragment
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
            Log.d("localdoujins", "switchFragment(LocalDoujinsFragment.newInstance())")
        }
    }

    override fun onResume() {
        super.onResume()
        val fragmentTag = supportFragmentManager
            .findFragmentById(R.id.container)?.tag ?: ""

        setTitle(fragmentTag)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragmentTag = supportFragmentManager
            .findFragmentById(R.id.container)?.tag ?: ""

        when (item.itemId) {
            R.id.nav_localdoujins -> {
                if (fragmentTag != LocalDoujinsFragment.APPBAR_TITLE) {
                    val fragment = LocalDoujinsFragment.newInstance()
                    addFragment(fragment, fragment.getTitle())
                    setTitle(fragment.getTitle())
                }
            }

            R.id.nav_online -> {
                if (fragmentTag != OnlineDoujinFragment.APPBAR_TITLE) {
                    val fragment = OnlineDoujinFragment.newInstance()
                    pushFragment(fragment, fragment.getTitle())
                    setTitle(fragment.getTitle())
                }
            }

            R.id.nav_favourites -> {
                if (fragmentTag != CollectionDoujinFragment.APPBAR_TITLE) {
                    val fragment = CollectionDoujinFragment.newInstance()
                    pushFragment(fragment, fragment.getTitle())
                    setTitle(fragment.getTitle())
                }
            }

            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        item.isChecked = true

        drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    private fun pushFragment(fragment: Fragment, tagName: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, tagName)
            .addToBackStack("stack")
            .commit()
    }

    // Adds fragment without pushing to backstack
    private fun addFragment(fragment: Fragment, tagName: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, tagName)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            val first = supportFragmentManager.getBackStackEntryAt(0)
            supportFragmentManager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)

            setTitle(LocalDoujinsFragment.APPBAR_TITLE)
        }
    }
}
