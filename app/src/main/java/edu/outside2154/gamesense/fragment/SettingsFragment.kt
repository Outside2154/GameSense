package edu.outside2154.gamesense.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner

import edu.outside2154.gamesense.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SettingsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.run {
            mParam1 = getString(ARG_PARAM1)
            mParam2 = getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_settings, container, false)

        val atkSpinner = v.findViewById<Spinner>(R.id.atk_spinner) as Spinner
        val atkOptions = arrayOf<String>("Sleeping", "Relaxing", "Eating", "Cleaning", "Social: With Friends/Family")
        val atkAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, atkOptions)

        val intSpinner = v.findViewById<Spinner>(R.id.int_spinner) as Spinner
        val intOptions = arrayOf<String>("Occupation: Studying/Working", "Reading", "Playing Musical Instrument", "Drawning", "Singing")
        val intAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, intOptions)

        val hthSpinner = v.findViewById<Spinner>(R.id.hth_spinner) as Spinner
        val hthOptions = arrayOf<String>("Exercising", "Walking", "Running", "Hiking", "Lifting Weights", "Yoga")
        val hthAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, hthOptions)

        atkSpinner.setAdapter(atkAdapter)
        intSpinner.setAdapter(intAdapter)
        hthSpinner.setAdapter(hthAdapter)
        return v
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
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): SettingsFragment {
            val fragment = SettingsFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
            return fragment
        }
    }
}// Required empty public constructor
