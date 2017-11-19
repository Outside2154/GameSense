package edu.outside2154.gamesense.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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

    override fun update() {
        // Update all progress bars
        player?.let {
            health_activity_value.text = it.regenStat.goals.items.keys.first().toString()
            attack_activity_value.text = it.atkStat.goals.items.keys.first().toString()
            intel_activity_value.text = it.intStat.goals.items.keys.first().toString()

            health_user_amt_value.text = it.regenStat.current.items.values.first().toString()
            attack_user_amt_value.text = it.atkStat.current.items.values.first().toString()
            intel_user_amt_value.text = it.intStat.current.items.values.first().toString()

            health_goal_amt_value.text = it.regenStat.goals.items.values.first().toString()
            attack_goal_amt_value.text = it.atkStat.goals.items.values.first().toString()
            intel_goal_amt_value.text = it.intStat.goals.items.values.first().toString()

            health_progress.progress = it.health.toInt()
            attack_progress.progress = it.atkStat.calcStat()?.toIntPercent() ?: 0
            intel_progress.progress = it.intStat.calcStat()?.toIntPercent() ?: 0
        }
    }
}
