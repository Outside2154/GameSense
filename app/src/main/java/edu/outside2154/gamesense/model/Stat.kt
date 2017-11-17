package edu.outside2154.gamesense.model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import edu.outside2154.gamesense.database.FirebaseTransform

class Stat (initGoals : Map<String, Double>, currGoals : Map<String, Double>) {
    data class StatItems(val items: Map<String, Double>) {
        operator fun plus(other: StatItems): StatItems {
            return StatItems(items.mapValues { (k, v) ->
                v + (other.items[k] ?: 0.0)
            })
        }
    }

    @Suppress("UNCHECKED_CAST")
    class FirebaseStatTransform : FirebaseTransform<Stat> {
        override fun fromFirebase(s: DataSnapshot): Stat? {
            val goals = s.child("goals").value as Map<String, Long>? ?: return null
            val current = s.child("current").value as Map<String, Long>? ?: return null
            return Stat(convertMap(goals), convertMap(current))
        }
        override fun toFirebase(value: Stat, ref: DatabaseReference) {
            ref.child("goals").updateChildren(value.goals.items)
            ref.child("current").updateChildren(value.current.items)
        }

        private fun convertMap(origMap : Map<String, Long>): Map<String, Double> =
                origMap.mapValues {it.value.toDouble()}
    }

    var goals = StatItems(initGoals)
    var current = StatItems(currGoals)

    fun updateCurrent(data: Map<String, Double>) {
        current += StatItems(data)
    }

    fun calcStat(): Double? {
        val divisor = goals.items.values.sum()
        if (divisor == 0.0) return null

        val elements = current.items.mapValues { (k, v) ->
            minOf(v, goals.items[k] ?: Double.MAX_VALUE)
        }

        print(elements)

        return elements.values.sum() / divisor
    }

    fun reset() {
        current = StatItems(current.items.mapValues { (_, v) -> v * 0.0 })
    }
}
