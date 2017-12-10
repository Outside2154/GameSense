package edu.outside2154.gamesense.model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.outside2154.gamesense.database.BoundFirebaseProperty
import edu.outside2154.gamesense.database.FirebaseRefSnap
import edu.outside2154.gamesense.database.firebaseListen
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

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

    private fun writeObject(s: ObjectOutputStream) = s.run {
        writeObject(message)
        writeBoolean(read)
    }

    private fun readObject(s: ObjectInputStream) = s.run {
        message = readObject() as String
        read = readBoolean()
    }
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