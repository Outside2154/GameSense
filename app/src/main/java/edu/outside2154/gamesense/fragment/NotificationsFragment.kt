package edu.outside2154.gamesense.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListAdapter
import android.widget.Spinner
import edu.outside2154.gamesense.R
import kotlinx.android.synthetic.main.fragment_notifications.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NotificationsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NotificationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: NotificationsFragment.OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.run {
            mParam1 = getString(NotificationsFragment.ARG_PARAM1)
            mParam2 = getString(NotificationsFragment.ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_settings, container, false)

//        val recyclerView = v.findViewById<RecyclerView>(R.id.recycle_view) as RecyclerView
//        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity) as RecyclerView.LayoutManager

        var messages = arrayOf<String>("Welcome to our game!")


        //val recyclerViewAdapter =RecyclerView.Adapter<String>(context, android.R.layout.simple_list_item_1, messages)

        recycle_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            //setAdapter(recyclerViewAdapter)
        }


        return v
    }

//    fun getNotifications(): ArrayList<String>{
//        var list = ArrayList<String>()
//        list.add("Welcome to our game!")
//        return list
//    }

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
         * @return A new instance of fragment NotificationsFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): NotificationsFragment {
            val fragment = NotificationsFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
            return fragment
        }
    }


}

//data class Notification(val message: ArrayList<String>, val read : ArrayList<Int>)