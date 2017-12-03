package edu.outside2154.gamesense.model

import edu.outside2154.gamesense.database.BoundFirebaseProperty
import edu.outside2154.gamesense.database.FirebaseRefSnap
<<<<<<< HEAD
=======
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
>>>>>>> master
import java.io.Serializable

interface Timestamps : Serializable {
    var lastBattleTime: Int
    var lastResetTime: Int
}

abstract class TimestampsBaseImpl : Timestamps {
    abstract override var lastBattleTime: Int
    abstract override var lastResetTime: Int
}

class TimestampsFirebaseImpl(root: FirebaseRefSnap) : TimestampsBaseImpl() {
    override var lastBattleTime: Int by BoundFirebaseProperty(root, 0)
    override var lastResetTime: Int by BoundFirebaseProperty(root, 0)
<<<<<<< HEAD
=======

    private fun writeObject(s: ObjectOutputStream) = s.run {
        writeInt(lastBattleTime)
        writeInt(lastResetTime)
    }

    private fun readObject(s: ObjectInputStream) = s.run {
        lastBattleTime = readInt()
        lastResetTime = readInt()
    }
>>>>>>> master
}
