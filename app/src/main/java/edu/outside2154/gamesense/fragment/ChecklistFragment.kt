package edu.outside2154.gamesense.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import edu.outside2154.gamesense.R
import edu.outside2154.gamesense.model.*
import edu.outside2154.gamesense.util.BundleUpdatable
import edu.outside2154.gamesense.util.Updatable
import edu.outside2154.gamesense.util.toIntPercent
import kotlinx.android.synthetic.main.fragment_checklist.*
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChecklistFragment : Fragment(), Updatable, BundleUpdatable {
    private var player: Player? = null
    private var boss: Boss? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateBundle(arguments)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_checklist, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        update()
    }

    override fun updateBundle(bundle: Bundle) {
        bundle.run {
            player = getSerializable("player") as Player?
            boss = getSerializable("boss") as Boss?
        }
    }

    private fun updateText(stat: Stat,
                           activityValue: TextView,
                           userAmtValue: TextView,
                           goalAmtValue: TextView) {
        try {
            activityValue.text = stat.goals.items.keys.first().toString()
            userAmtValue.text = stat.current.items.values.first().toString()
            goalAmtValue.text = stat.goals.items.values.first().toString()
        } catch (e: NoSuchElementException) {
            activityValue.text = "No goal :("
            userAmtValue.text = "0"
            goalAmtValue.text = "0"
        }
    }

    override fun update() {
        // Update all progress bars
        player?.let {
            updateText(
                    it.regenStat,
                    health_activity_value,
                    health_user_amt_value,
                    health_goal_amt_value)
            updateText(
                    it.atkStat,
                    attack_activity_value,
                    attack_user_amt_value,
                    attack_goal_amt_value)
            updateText(
                    it.intStat,
                    intel_activity_value,
                    intel_user_amt_value,
                    intel_goal_amt_value)

            health_progress.progress = it.regenStat.calcStat()?.toIntPercent() ?: 0
            attack_progress.progress = it.atkStat.calcStat()?.toIntPercent() ?: 0
            intel_progress.progress = it.intStat.calcStat()?.toIntPercent() ?: 0
        }
    }
}
