package edu.outside2154.gamesense.model

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.outside2154.gamesense.model.Stat
import edu.outside2154.gamesense.model.Boss
import java.io.Serializable
import java.util.*

const val PLAYER_BASE_HEALTH = 100.0
const val PLAYER_BASE_ATTACK = 100.0
const val PLAYER_CRIT_MULT = 2.0


class Player(androidId : String) : Serializable {
    var health = PLAYER_BASE_HEALTH
        private set

    var intStat : Stat? = null
    var atkStat : Stat? = null
    var regenStat : Stat? = null

    var currency = 0.0
        private set
    val pureDamage
        get() = PLAYER_BASE_ATTACK * (atkStat?.calcStat() ?: 0.0)
    val dead
        get() = health == 0.0
    private val rand = Random()

    init {
        // Create listener for data reading from Firebase
        val fbListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get snapshot of health goals and current values
                val goalsHealthSnapShot = snapshot.child("health").child("goals")
                val currHealthSnapShot = snapshot.child("health").child("current")

                // Get snapshot of attack goals and current values
                val goalsAtkSnapShot = snapshot.child("attack").child("goals")
                val currAtkSnapShot = snapshot.child("attack").child("current")

                // Get snapshot of intelligence goals and current values
                val goalsIntSnapShot = snapshot.child("intelligence").child("goals")
                val currIntSnapShot = snapshot.child("intelligence").child("current")


                // Set stats as maps of goals and current values
                regenStat = Stat(convertMap(goalsHealthSnapShot.getValue() as Map<String, Long>),
                        convertMap(currHealthSnapShot.getValue() as Map<String, Long>))
                atkStat = Stat(convertMap(goalsAtkSnapShot.getValue() as Map<String, Long>),
                        convertMap(currAtkSnapShot.getValue() as Map<String, Long>))
                intStat = Stat(convertMap(goalsIntSnapShot.getValue() as Map<String, Long>),
                        convertMap(currIntSnapShot.getValue() as Map<String, Long>))

                // Get snapshot of current health and cast appropriately
                val currHealthSnapshot = snapshot.child("character").child("health")
                val longHealth = currHealthSnapshot.getValue() as Long
                health = longHealth.toDouble()

                // Get snapshot of current currency and cast appropriately
                val currCurrencySnapshot = snapshot.child("character").child("currency")
                val longCurrency = currCurrencySnapshot.getValue() as Long
                currency = longCurrency.toDouble()
            }

            // Print if error occurs
            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }

        // Grab values with single value event listener
        val dbRef = FirebaseDatabase.getInstance().getReference()
        dbRef.child(androidId).addListenerForSingleValueEvent(fbListener)
    }

    private fun convertMap(origMap : Map<String, Long>): Map<String, Double> {
        return origMap.mapValues {it.value.toDouble()}
    }

    private fun isCritical(): Boolean{
        return rand.nextDouble() < intStat?.calcStat() ?: 0.0
    }

    private fun takeDamage(damage: Double){
        health = maxOf(health - damage, 0.0)
    }

    fun fight(boss: Boss){
        if (boss.dead) return

        // User attacks
        var finalDamage = pureDamage
        if (isCritical()) finalDamage *= PLAYER_CRIT_MULT
        boss.takeDamage(finalDamage)

        // Boss attacks
        if (boss.dead) return
        takeDamage(boss.attack)
    }
}
