package edu.outside2154.gamesense.activity

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem

import edu.outside2154.gamesense.R
import edu.outside2154.gamesense.database.FromFirebaseAndUpdate
import edu.outside2154.gamesense.fragment.ChecklistFragment
import edu.outside2154.gamesense.fragment.HomeFragment
import edu.outside2154.gamesense.fragment.SettingsFragment
import edu.outside2154.gamesense.model.*
import edu.outside2154.gamesense.util.*
import kotlinx.android.synthetic.main.activity_nav.*
import kotlinx.android.synthetic.main.toolbar.*

class NavActivity : AppCompatActivity(), Updatable {
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private var fragment: Fragment? = null

    private val androidId = getAndroidId(this)
    private val player: Player? by FromFirebaseAndUpdate(androidId, ::PlayerFirebaseImpl)
    private val boss: Boss? by FromFirebaseAndUpdate("$androidId/boss", ::BossFirebaseImpl)

    private val notifications = Notifications()

    fun makeBundle(): Bundle = Bundle().apply {
        putSerializable("player", player)
        putSerializable("boss", boss)
        putInt("notificationCount", notifications.notificationCount)
        putSerializable("notifications", notifications)
    }

    override fun update() {
        // Try to update the bundle, then try to update.
        (fragment as? BundleUpdatable)?.updateBundle(makeBundle())
        (fragment as? Updatable)?.update()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        notifications.getNotifications(androidId, notifications)

        // Set a Toolbar to replace the ActionBar.
        setSupportActionBar(gs_toolbar)

        // Find and setup the drawer view.
        nvView.setNavigationItemSelectedListener {
            selectDrawerItem(it)
            true  // Display the item as selected.
        }

        // Tie DrawerLayout events to a toggle.
        drawerToggle = ActionBarDrawerToggle(
                this, drawer_layout, gs_toolbar,
                R.string.drawer_open, R.string.drawer_close)
        drawer_layout.addDrawerListener(drawerToggle)

        // Initially select the first menu item.
        selectDrawerItem(nvView.menu.getItem(0))
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        // Create a new fragment based on menu item selected.
        fragment = when (menuItem.itemId) {
            R.id.nav_home_fragment -> HomeFragment()
            R.id.nav_settings_fragment -> SettingsFragment()
            R.id.nav_checklist_fragment -> ChecklistFragment()
            else -> Fragment()  // TODO: replace with a 404 fragment
        }

        // Add player/boss objects to bundle along with androidId
        fragment?.arguments = makeBundle()

        // Insert the fragment by replacing any existing fragment
        supportFragmentManager.transact { replace(R.id.flContent, fragment) }

        // Update UI state
        menuItem.isChecked = true
        title = menuItem.title
        drawer_layout.closeDrawers()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles.
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // The action bar home/up action should open or close the drawer.
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)

    }
}