package edu.outside2154.gamesense.util

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import edu.outside2154.gamesense.database.BoundFirebaseProperty
import edu.outside2154.gamesense.database.FirebaseRefSnap
import edu.outside2154.gamesense.database.FirebaseTransform
import java.util.*

interface DataHandler {
    fun pullData(): Map<String, Double>
}

abstract class DataHandlerBaseImpl(val user: ExtraSensoryUser) : DataHandler {
    abstract var lastUpdateTime: Date
    private val freshData: Map<String, Double>
        get() = user.files
                .filter { it.creationTime.after(lastUpdateTime) }
                .mapNotNull { it.info?.topPrediction }
                .groupingBy { it }
                .eachCount()
                .mapValues { (_, v) -> v / 60.0 }

    override fun pullData(): Map<String, Double> {
        val toReturn = freshData
        lastUpdateTime = Calendar.getInstance().time
        return toReturn
    }
}

class DataHandlerLocalImpl(user: ExtraSensoryUser,
                           override var lastUpdateTime: Date) : DataHandlerBaseImpl(user)

class DataHandlerFirebaseImpl(user: ExtraSensoryUser,
                              root: FirebaseRefSnap) : DataHandlerBaseImpl(user) {
    override var lastUpdateTime: Date by BoundFirebaseProperty(
            root, Calendar.getInstance().time, FirebaseDateTransform())
}

class FirebaseDateTransform : FirebaseTransform<Date> {
    override fun fromFirebase(s: DataSnapshot): Date? {
        val timestamp = s.value as? Long ?: return null
        return Date(timestamp)
    }

    override fun toFirebase(value: Date, ref: DatabaseReference) {
        ref.setValue(value.time)
    }
}
