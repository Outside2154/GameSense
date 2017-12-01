package edu.outside2154.gamesense.fragment

//import org.jetbrains.anko.*
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import edu.outside2154.gamesense.R
import edu.outside2154.gamesense.model.Boss
import edu.outside2154.gamesense.model.Notification
import edu.outside2154.gamesense.model.Notifications
import edu.outside2154.gamesense.model.Player
import edu.outside2154.gamesense.util.BundleUpdatable
import edu.outside2154.gamesense.util.Updatable
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import kotlinx.android.synthetic.main.fragment_notifications_row.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NotificationsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NotificationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationsFragment : Fragment(), Updatable, BundleUpdatable {
    // TODO: Rename and change types of parameters
    private var messages: Notifications? = null
    private var strings = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateBundle(arguments)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        update()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_notifications, container, false)

        val notifications = v.findViewById<RecyclerView>(R.id.recycler_view) as RecyclerView

        notifications.layoutManager = LinearLayoutManager(context)
        notifications.adapter = NotificationsAdapter(strings)
        return v
    }

    override fun updateBundle(bundle: Bundle) {
        bundle.run {
            messages = getSerializable("notifications") as Notifications?

        }
    }

    override fun update() {
        messages?.let {
            val list: MutableList<Notification> = it.notifications

            for (item in list) {
                if (item.message.isNotEmpty())
                    strings.add(item.message)
            }
        }
    }

}