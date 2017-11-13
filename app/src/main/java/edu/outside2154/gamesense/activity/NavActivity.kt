package edu.outside2154.gamesense.activity

import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.view.View
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem

import edu.outside2154.gamesense.Character
import edu.outside2154.gamesense.Boss

import edu.outside2154.gamesense.R
import edu.outside2154.gamesense.fragment.ChecklistFragment
import edu.outside2154.gamesense.fragment.HomeFragment
import edu.outside2154.gamesense.fragment.SettingsFragment
import edu.outside2154.gamesense.isEmulator

// TODO: Review this.
class NavActivity : AppCompatActivity() {
    private var mDrawer: DrawerLayout? = null
    private var toolbar: Toolbar? = null
    private var nvDrawer: NavigationView? = null

    // Make sure to be using android.support.v7.app.ActionBarDrawerToggle version.
    // The android.support.v4.app.ActionBarDrawerToggle has been deprecated.
    private var drawerToggle: ActionBarDrawerToggle? = null

    private var character: Character? = null
    private var boss: Boss? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val tx = supportFragmentManager.beginTransaction()
        tx.replace(R.id.flContent, HomeFragment())
        tx.commit()

        // Find our drawer view
        mDrawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        nvDrawer = findViewById<View>(R.id.nvView) as NavigationView
        // Setup drawer view
        setupDrawerContent(nvDrawer)

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        // Find our drawer view
        mDrawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawerToggle = setupDrawerToggle()

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer!!.addDrawerListener(drawerToggle!!)

        // Grab current androidID
        var androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID); //Device ID
        if (isEmulator()) {
            androidId = "1cf08e3503018df0";
        }

        // Create character and boss objects for use in all fragments
        character = Character(androidId)
        boss = Boss(androidId)
    }

    private fun setupDrawerToggle(): ActionBarDrawerToggle {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        drawerToggle!!.onConfigurationChanged(newConfig)
    }

    private fun setupDrawerContent(navigationView: NavigationView?) {
        navigationView!!.setNavigationItemSelectedListener { menuItem ->
            selectDrawerItem(menuItem)
            true
        }
    }

    fun selectDrawerItem(menuItem: MenuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        var fragment: Fragment? = null
        val fragmentClass: Class<*>
        when (menuItem.itemId) {
            R.id.nav_first_fragment -> fragmentClass = HomeFragment::class.java
            R.id.nav_second_fragment -> fragmentClass = SettingsFragment::class.java
            R.id.nav_third_fragment -> fragmentClass = ChecklistFragment::class.java
            else -> fragmentClass = HomeFragment::class.java
        }

        try {
            // Pass character and boss objects as Serializable objects to fragments
            var bundle = Bundle()
            bundle.putSerializable("char", character)
            bundle.putSerializable("boss", boss)
            fragment = fragmentClass.newInstance() as Fragment
            fragment.setArguments(bundle)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Insert the fragment by replacing any existing fragment
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit()

        // Highlight the selected item has been done by NavigationView
        menuItem.isChecked = true
        // Set action bar title
        title = menuItem.title
        // Close the navigation drawer
        mDrawer!!.closeDrawers()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // The action bar home/up action should open or close the drawer.
        return if (drawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)

    }
}