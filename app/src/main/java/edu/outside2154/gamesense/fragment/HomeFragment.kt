package edu.outside2154.gamesense.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import edu.outside2154.gamesense.R
import java.net.URI

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

    private lateinit var atkPb: ProgressBar
    private lateinit var intelPb: ProgressBar
    private lateinit var hthPb: ProgressBar

    private lateinit var atkAmt: TextView
    private lateinit var intelAmt: TextView
    private lateinit var hthAmt: TextView

//    private lateinit var atkSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.run {
            mParam1 = getString(ARG_PARAM1)
            mParam2 = getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)

        // TODO: Don't hardcode.
//        val atkLevel = 20
//        atkPb = v.findViewById(R.id.atk_pb)
//        atkPb.progress = atkLevel
//        atkAmt = v.findViewById(R.id.atk_amt)
//        atkAmt.text = atkLevel.toString() + "%"
//        val intelLevel = 30
//        intelPb = v.findViewById(R.id.intel_pb)
//        intelPb.progress = intelLevel
//        intelAmt = v.findViewById(R.id.intel_amt)
//        intelAmt.text = intelLevel.toString() + "%"
//        val hthLevel = 40
//        hthPb = v.findViewById(R.id.hth_pb)
//        hthPb.progress = hthLevel
//        hthAmt = v.findViewById(R.id.hth_amt)
//        hthAmt.text = hthLevel.toString() + "%"

        val atkSpinner = v.findViewById<Spinner>(R.id.atk_spinner) as Spinner
        val strings = arrayOf<String>("a", "b", "c")
        val adapater = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, strings)

        atkSpinner.setAdapter(adapater)




        // Inflate the layout for this fragment
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
