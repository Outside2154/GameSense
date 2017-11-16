package edu.outside2154.gamesense.activity

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import com.google.firebase.database.FirebaseDatabase

import edu.outside2154.gamesense.model.Player
import edu.outside2154.gamesense.model.Boss

import edu.outside2154.gamesense.R
import edu.outside2154.gamesense.fragment.ChecklistFragment
import edu.outside2154.gamesense.fragment.HomeFragment
import edu.outside2154.gamesense.fragment.SettingsFragment
import edu.outside2154.gamesense.util.transact

class NavActivity : AppCompatActivity() {
    private lateinit var mDrawer: DrawerLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle

    private var player: Player? = null
    private var boss: Boss? = null
    private lateinit var androidId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        // Set a Toolbar to replace the ActionBar.
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Find and setup the drawer view.
        mDrawer = findViewById(R.id.drawer_layout)
        val nvDrawer = findViewById<NavigationView>(R.id.nvView)
        nvDrawer.setNavigationItemSelectedListener {
            selectDrawerItem(it)
            true  // Display the item as selected.
        }

        // Tie DrawerLayout events to a toggle.
        drawerToggle = ActionBarDrawerToggle(
                this, mDrawer, toolbar,
                R.string.drawer_open, R.string.drawer_close)
        mDrawer.addDrawerListener(drawerToggle)

        // Grab androidId
        androidId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        if (isEmulator()) {
            androidId = "1cf08e3503018df0"
        }

        // Initially select the first menu item.
        selectDrawerItem(nvDrawer.menu.getItem(0))
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        // Create a new fragment based on menu item selected.
        val fragment = when (menuItem.itemId) {
            R.id.nav_home_fragment -> HomeFragment()
            R.id.nav_settings_fragment -> SettingsFragment()
            R.id.nav_checklist_fragment -> ChecklistFragment()
            else -> Fragment()  // TODO: replace with a 404 fragment
        }

        // Add player/boss objects to bundle along with androidId
        val bundle = Bundle()
        bundle.putSerializable("player", player)
        bundle.putSerializable("boss", boss)
        bundle.putString("androidId", androidId)
        fragment.arguments = bundle

        // Insert the fragment by replacing any existing fragment
        supportFragmentManager.transact { replace(R.id.flContent, fragment) }

        // Update UI state
        menuItem.isChecked = true
        title = menuItem.title
        mDrawer.closeDrawers()
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

    override fun onStop() {
        updateFirebase()
        super.onStop()
    }

    fun updateCharacters(inputPlayer : Player?, inputBoss : Boss?) {
        player = inputPlayer
        boss = inputBoss
    }

    private fun updateFirebase() {
        val dbRef = FirebaseDatabase.getInstance().reference

        // Update stats (goals and current)
        dbRef.child("attack").child("goals").updateChildren(player!!.atkStat.goals.items)
        dbRef.child("attack").child("current").updateChildren(player!!.atkStat.current.items)

        dbRef.child("intelligence").child("goals").setValue(player!!.intStat.goals.items)
        dbRef.child("intelligence").child("current").setValue(player!!.intStat.current.items)

        dbRef.child("regen").child("goals").setValue(player!!.regenStat.goals.items)
        dbRef.child("regen").child("current").setValue(player!!.regenStat.current.items)

        // Update character health and currency
        dbRef.child(androidId).child("character").child("health").setValue(player!!.health)
        dbRef.child(androidId).child("character").child("currency").setValue(player!!.currency)
    }

    private fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
            || "google_sdk" == Build.PRODUCT)
    }
}