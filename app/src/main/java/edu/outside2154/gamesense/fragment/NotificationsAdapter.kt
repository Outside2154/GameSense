package edu.outside2154.gamesense.fragment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import edu.outside2154.gamesense.R
import org.jetbrains.anko.find

class NotificationsAdapter (val messages: MutableList<String>): RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.message.text = messages[position]
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent?.context).inflate(R.layout.fragment_notifications_row, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.find<TextView>(R.id.message)
        init {

        }
    }
}