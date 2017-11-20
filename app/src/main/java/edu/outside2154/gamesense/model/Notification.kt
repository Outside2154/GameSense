package edu.outside2154.gamesense.model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.outside2154.gamesense.database.BoundFirebaseProperty
import edu.outside2154.gamesense.database.FirebaseRefSnap
import edu.outside2154.gamesense.database.firebaseListen
import java.io.Serializable

/**
 * Created by TyDewes on 11/18/17.
 */

interface Notification : Serializable {
    val message: String
    val read: Boolean
}

abstract class NotificationBaseImpl : Notification {
    abstract override var message: String
    abstract override var read: Boolean
}

class NotificationFirebaseImpl(root: FirebaseRefSnap) : NotificationBaseImpl() {
    override var message: String by BoundFirebaseProperty(root, "")
    override var read: Boolean by BoundFirebaseProperty(root, false)
}

class Notifications() : Serializable {
    var notificationCount = 0
    val notifications = mutableListOf<Notification>()

    fun getNotifications(androidId: String, notifications: Notifications) {
        val childListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val count = dataSnapshot.childrenCount.toInt()
                notifications.notificationCount = count
                createNotifications(count, androidId)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        FirebaseDatabase.getInstance().reference.child("$androidId/messages").addListenerForSingleValueEvent(childListener)
    }

    private fun createNotifications(notificationCount: Int, androidId: String) {
        for (i in 0..notificationCount) {
            firebaseListen("$androidId/messages/message_$i") {
                this.notifications.add(NotificationFirebaseImpl(it))
            }
        }
    }
}