package edu.outside2154.gamesense.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import edu.outside2154.gamesense.R
import edu.outside2154.gamesense.model.Boss
import edu.outside2154.gamesense.model.Player
import edu.outside2154.gamesense.model.Stat
import edu.outside2154.gamesense.util.BundleUpdatable
import edu.outside2154.gamesense.util.Updatable
import edu.outside2154.gamesense.util.getAndroidId
import kotlinx.android.synthetic.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SettingsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment(), Updatable, BundleUpdatable {
    private var player: Player? = null
    private var updateInt = false
    private var updateAtk = false
    private var updateHth = false
    private var intGoal: String = ""
    private var atkGoal: String = ""
    private var hthGoal: String = ""
    private var atkSpinner: Spinner? = null
    private var intSpinner: Spinner? = null
    private var hthSpinner: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateBundle(arguments)
    }

    override fun updateBundle(bundle: Bundle) {
        bundle.run {
            player = getSerializable("player") as Player?
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val v = inflater.inflate(R.layout.fragment_settings, container, false)


        atkSpinner = v.findViewById<Spinner>(R.id.atk_spinner) as Spinner
        val atkOptions = arrayOf<String>("Exercising", "Walking", "Running", "Hiking", "Lifting Weights", "Yoga")
        val atkAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, atkOptions)

        intSpinner = v.findViewById<Spinner>(R.id.int_spinner) as Spinner
        val intOptions = arrayOf<String>("Reading", "Playing Musical Instrument", "Drawing", "Singing")
        val intAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, intOptions)

        hthSpinner = v.findViewById<Spinner>(R.id.hth_spinner) as Spinner
        val hthOptions = arrayOf<String>("Sleeping", "Relaxing", "Eating", "Cleaning", "Social: With Friends/Family")

        val hthAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, hthOptions)

        atkSpinner?.setAdapter(atkAdapter)
        intSpinner?.setAdapter(intAdapter)
        hthSpinner?.setAdapter(hthAdapter)

        val btnSubmit = v.findViewById<Button>(R.id.submit_settings) as Button
        val atkHours = v.findViewById<EditText>(R.id.atk_hours) as EditText
        val intHours = v.findViewById<EditText>(R.id.int_hours) as EditText
        val hthHours = v.findViewById<EditText>(R.id.hth_hours) as EditText

        btnSubmit.setOnClickListener {
            atkGoal = atkHours.text.toString().trim()
            intGoal = intHours.text.toString().trim()
            hthGoal = hthHours.text.toString().trim()
            if(atkGoal.length > 0) {
                updateAtk = true
            }
            if(intGoal.length > 0) {
                updateInt = true
            }
            if(hthGoal.length > 0) {
                updateHth = true
            }
            if (atkGoal.length > 0 || intGoal.length > 0 || hthGoal.length > 0) {
                update()
                Toast.makeText(getActivity(), "Goals have been updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(getActivity(), "Please select an option!", Toast.LENGTH_SHORT).show()
            }
        }
        return v

    }

    override fun update() {
            val androidId = getAndroidId(activity)
                if (updateAtk) {
                    FirebaseDatabase.getInstance().reference.child("$androidId/atkStat/current/").removeValue()
                    FirebaseDatabase.getInstance().reference.child("$androidId/atkStat/goals/").removeValue()
                    var atkInit = mutableMapOf<String, Double>()
                    atkInit.put(atkSpinner?.getSelectedItem().toString(), atkGoal.toDouble())
                    var atkCurr = mutableMapOf<String, Double>()
                    atkCurr.put(atkSpinner?.getSelectedItem().toString(), 0.0)
                    player?.atkStat = Stat(atkInit, atkCurr)
                }
                if (updateHth) {
                    FirebaseDatabase.getInstance().reference.child("$androidId/regenStat/current/").removeValue()
                    FirebaseDatabase.getInstance().reference.child("$androidId/regenStat/goals/").removeValue()
                    val hthInit = mutableMapOf<String, Double>()
                    hthInit.put(hthSpinner?.getSelectedItem().toString(), hthGoal.toDouble())
                    val hthCurr = mutableMapOf<String, Double>()
                    hthCurr.put(hthSpinner?.getSelectedItem().toString(), 0.0)
                    player?.regenStat = Stat(hthInit, hthCurr)
                }
                if (updateInt) {
                    FirebaseDatabase.getInstance().reference.child("$androidId/intStat/current/").removeValue()
                    FirebaseDatabase.getInstance().reference.child("$androidId/intStat/goals/").removeValue()
                    val intInit = mutableMapOf<String, Double>()
                    intInit.put(intSpinner?.getSelectedItem().toString(), intGoal.toDouble())
                    val intCurr = mutableMapOf<String, Double>()
                    intCurr.put(intSpinner?.getSelectedItem().toString(), 0.0)
                    player?.intStat = Stat(intInit, intCurr)
                }

    }
}
