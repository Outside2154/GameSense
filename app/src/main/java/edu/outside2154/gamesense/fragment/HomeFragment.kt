package edu.outside2154.gamesense.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import edu.outside2154.gamesense.R
import edu.outside2154.gamesense.database.BoundFirebaseProperty
import edu.outside2154.gamesense.database.FirebaseRefSnap
import edu.outside2154.gamesense.database.FromFirebaseAndUpdate
import edu.outside2154.gamesense.database.firebaseListen
import edu.outside2154.gamesense.model.*
import edu.outside2154.gamesense.util.BundleUpdatable
import edu.outside2154.gamesense.util.Updatable
import edu.outside2154.gamesense.util.getAndroidId
import edu.outside2154.gamesense.util.toIntPercent
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), Updatable, BundleUpdatable {
    private val androidId = getAndroidId(activity) as FirebaseRefSnap
    private var player: Player? = null
    private var boss: Boss? = null
    private var messages: Notifications? = null
    private var lastBattleTime: Int by BoundFirebaseProperty(androidId, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateBundle(arguments)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        update()
    }

    override fun updateBundle(bundle: Bundle) {
        bundle.run {
            player = getSerializable("player") as Player?
            boss = getSerializable("boss") as Boss?
            messages = getSerializable("notifications") as Notifications?

        }
    }

    override fun update() {
        var timeDiff = System.currentTimeMillis()/1000 - lastBattleTime

        if (lastBattleTime != 0 && timeDiff < 86400) {
            var fightData = player?.fight(boss!!)

//            var fightData = Triple(0, 5.0, 10.0)

            if (fightData?.first == 1)
                createNotification("Congratulations! You killed the boss!")

            else {
                createNotification("You did " + fightData?.second.toString() + " damage to the boss!")
                createNotification("The boss did " + fightData?.third.toString() + " damage to you!")
            }

            if (player?.dead == true)
                createNotification("You're dead! Game over!")
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
