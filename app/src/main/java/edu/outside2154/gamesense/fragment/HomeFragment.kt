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
    private var player: Player? = null
    private var boss: Boss? = null

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
        }
    }

    override fun update() {
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
}// Required empty public constructor
