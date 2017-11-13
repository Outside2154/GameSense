package edu.outside2154.gamesense

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.Serializable
import java.util.*


/**
 * Created by Nurbergen on 11/4/17.
 * Modified by Tynan Dewes on 11/13/17
 */

class Character (androidId : String): Serializable {
    private val baseHealth = 100.0
    private val baseAttack = 100.0
    private val criticalMultiplier = 2.0

    private var health = baseHealth
    private var regenStat: Stat? = null
    private var atkStat: Stat? = null
    private var intStat: Stat? = null

    private var currency = 0.0
    private var avatar = ""

    init {
        val dbRef = FirebaseDatabase.getInstance().getReference()

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
                regenStat = Stat(goalsHealthSnapShot.getValue() as Map<String, Double>,
                        currHealthSnapShot.getValue() as Map<String, Double>)
                atkStat = Stat(goalsAtkSnapShot.getValue() as Map<String, Double>,
                        currAtkSnapShot.getValue() as Map<String, Double>)
                intStat = Stat(goalsIntSnapShot.getValue() as Map<String, Double>,
                        currIntSnapShot.getValue() as Map<String, Double>)

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
        dbRef.child(androidId).addListenerForSingleValueEvent(fbListener)
    }

    private fun isCritical(): Boolean{
        return Random().nextDouble() < intStat?.calcStat() ?: 0.0
    }

    private fun takeDamage(damage: Double){
        health = maxOf(health - damage, 0.0)
    }

    fun fightWith(boss: Boss){
        if (boss.isDead()) return

        // User attacks
        val attack = atkStat?.calcStat() ?: 0.0
        var damage = attack * baseAttack
        if (isCritical()) damage *= criticalMultiplier
        boss.takeDamage(damage)

        // Boss attacks
        if (boss.isDead()) return
        takeDamage(boss.getAttack())
    }

    fun isDead() : Boolean{
        return health == 0.0
    }

    fun getHealth() : Double {
        return health
    }

    fun getAttack() : Double? {
        return atkStat?.calcStat()
    }

    fun getIntelligence() : Double? {
        return intStat?.calcStat()
    }
}
