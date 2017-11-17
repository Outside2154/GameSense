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

import edu.outside2154.gamesense.R
import edu.outside2154.gamesense.activity.NavActivity
import edu.outside2154.gamesense.database.FirebaseRefSnap
import edu.outside2154.gamesense.database.firebaseListen
import edu.outside2154.gamesense.model.*
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

        if (player == null) {
            firebaseListen(androidId) {
                player = PlayerFirebaseImpl(it)
                updatePlayerBars()
                (activity as NavActivity).updatePlayer(player)
            }
        }

        // If we didn't have a boss, get it from Firebase.
        // Once we have the boss, update.
        if (boss == null) {
            firebaseListen("$androidId/boss") {
                boss = BossFirebaseImpl(it)
                updateBossBar()
                (activity as NavActivity).updateBoss(boss)
            }
        }
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
