package edu.outside2154.gamesense.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.outside2154.gamesense.model.Player
import edu.outside2154.gamesense.model.Boss

import edu.outside2154.gamesense.R
import edu.outside2154.gamesense.activity.NavActivity
import edu.outside2154.gamesense.database.FirebaseRefSnap
import edu.outside2154.gamesense.database.firebaseListen
import edu.outside2154.gamesense.model.BossFirebaseImpl
import edu.outside2154.gamesense.model.Stat
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    private var player: Player? = null
    private var boss: Boss? = null
    private lateinit var androidId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve items from bundle.
        arguments.run {
            androidId = getString("androidId")
            mParam1 = getString(ARG_PARAM1)
            mParam2 = getString(ARG_PARAM2)
            player = getSerializable("player") as Player?
            boss = getSerializable("boss") as Boss?
        }

        // If we didn't have a boss, get it from Firebase.
        // Once we have the boss, update.
        if (boss == null) {
            firebaseListen("$androidId/boss") {
                boss = BossFirebaseImpl(it)
                updateBossBar()
            }
        }

        if (player == null) createCharacters()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updatePlayerBars()
        updateBossBar()
    }

    private fun updatePlayerBars() {
        player?.let {
            hp_lb.progress = it.health.toInt()
            atk_lb.progress = ((it.atkStat.calcStat() ?: 0.0) * 100).toInt()
            int_lb.progress = ((it.intStat.calcStat() ?: 0.0) * 100).toInt()
        }
    }

    private fun updateBossBar() {
        boss?.let {
            boss_hp_lb.progress = it.health.toInt()
        }
    }

    private fun createCharacters() {
        @Suppress("UNCHECKED_CAST")
        firebaseListen(androidId) {
            val snapshot = it.snap

            // Get Maps of health goals and current values
            val healthGoals = snapshot.child("regen").child("goals").value as Map<String, Long>
            val healthCurr = snapshot.child("regen").child("current").value as Map<String, Long>

            // Get Maps of attack goals and current values
            val atkGoals = snapshot.child("attack").child("goals").value as Map<String, Long>
            val atkCurr = snapshot.child("attack").child("current").value as Map<String, Long>

            // Get Maps of intelligence goals and current values
            val intGoals = snapshot.child("intelligence").child("goals").value as Map<String, Long>
            val intCurr = snapshot.child("intelligence").child("current").value as Map<String, Long>

            // Set stats as maps of goals and current values
            val regenStat = Stat(convertMap(healthGoals), convertMap(healthCurr))
            val atkStat = Stat(convertMap(atkGoals), convertMap(atkCurr))
            val intStat = Stat(convertMap(intGoals), convertMap(intCurr))

            // Get snapshot of current health and cast appropriately
            val longHealth = snapshot.child("character").child("health").value as Long
            val health = longHealth.toDouble()

            // Get snapshot of current currency and cast appropriately
            val longCurrency = snapshot.child("character").child("currency").value as Long
            val currency = longCurrency.toDouble()

            // Get previous values for player
            player = Player(regenStat, atkStat, intStat, health, currency)
            updatePlayerBars()

            // Call ExtraSensory function
            // Fighting functions

            // Update player with new data
            // updatePlayerBars()

            (activity as NavActivity).updateCharacters(player, boss)
        }
    }

    private fun convertMap(origMap : Map<String, Long>): Map<String, Double> {
        return origMap.mapValues {it.value.toDouble()}
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) = mListener?.onFragmentInteraction(uri)


    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): HomeFragment {
            val fragment = HomeFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
            return fragment
        }
    }
}// Required empty public constructor
