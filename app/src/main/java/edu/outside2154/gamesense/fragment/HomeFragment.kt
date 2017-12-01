package edu.outside2154.gamesense.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import edu.outside2154.gamesense.R
import edu.outside2154.gamesense.database.*
import edu.outside2154.gamesense.model.*
import edu.outside2154.gamesense.util.BundleUpdatable
import edu.outside2154.gamesense.util.Updatable
import edu.outside2154.gamesense.util.getAndroidId
import edu.outside2154.gamesense.util.toIntPercent
import kotlinx.android.synthetic.main.fragment_home.*

const val ONE_DAY = 86400
const val ONE_WEEK = 604800

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), Updatable, BundleUpdatable {
    private lateinit var androidId : String
    private var player: Player? = null
    private var boss: Boss? = null
    private var messages: Notifications? = null
    private var timestamps: Timestamps? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateBundle(arguments)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_home, container, false)


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        update()
    }

    override fun updateBundle(bundle: Bundle) {
        bundle.run {
            player = getSerializable("player") as Player?
            boss = getSerializable("boss") as Boss?
            messages = getSerializable("notifications") as Notifications?
            timestamps = getSerializable("timestamps") as Timestamps?
        }
    }

    override fun update() {

        var currentTime = System.currentTimeMillis()/1000
        var resetTimeDiff = currentTime - (timestamps?.lastResetTime ?: 0)

        if ((timestamps?.lastResetTime ?: 0) != 0 && ONE_WEEK < resetTimeDiff) {
            if (boss?.dead == true) {
                createNotification("You beat the boss last week! Congratulations!")
                boss?.reset(true)
            }
            else {
                createNotification("You lost against the boss last week! Better luck this week.")
                boss?.reset(false)
            }

            player?.reset()

            timestamps?.lastResetTime = currentTime.toInt()
        }

        var battleTimeDiff = currentTime - (timestamps?.lastBattleTime ?: 0)

        if ((timestamps?.lastBattleTime ?: 0) != 0 && ONE_DAY < battleTimeDiff && player?.dead == false) {
            var fightData = player?.fight(boss!!)

            if (fightData?.first == 1)
                createNotification("Congratulations! You killed the boss!")

            else {
                createNotification("You did " + fightData?.second.toString() + " damage to the boss!")
                createNotification("The boss did " + fightData?.third.toString() + " damage to you!")
            }

            if (player?.dead == true)
                createNotification("You're dead! Game over!")

            timestamps?.lastBattleTime = currentTime.toInt()
        }

        // Update all progress bars
        player?.let {
            points_value.text = it.currency.toString()
            hp_lb.progress = it.health.toInt()
            atk_lb.progress = it.atkStat.calcStat()?.toIntPercent() ?: 0
            int_lb.progress = it.intStat.calcStat()?.toIntPercent() ?: 0
        }

        boss?.let {
            boss_level_value.text = it.level.toString()
            boss_hp_lb.progress = it.health.toInt()
            boss_atk_lb.progress = it.attack.toInt()
        }
    }

    fun createNotification(message: String) {
        var androidId = getAndroidId(activity)

        firebaseListen("$androidId/messages/message_" + messages?.notificationCount.toString()) {
            val notification = NotificationFirebaseImpl(it)
            notification.message = message
            notification.read = false

            messages?.notifications?.add(notification)
        }

        messages?.let {
            it.notificationCount++
        }
    }
}// Required empty public constructor
