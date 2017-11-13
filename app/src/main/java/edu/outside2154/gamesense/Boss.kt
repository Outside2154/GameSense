package edu.outside2154.gamesense

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable

/**
 * Created by Nurbergen on 11/4/17.
 * Modified by Tynan Dewes on 11/13/17
 */

class Boss (androidId : String): Serializable {
    private val baseHealth = 100.0
    private val healthInc = 50.0
    private val baseAttack = 20.0
    private val attackInc = 5.0

    private var health = baseHealth
    private var attack = baseAttack
    private var lvl = 1
    private var avatar = ""

    init {
        val dbRef = FirebaseDatabase.getInstance().getReference()

        // Create listener for data reading from Firebase
        val fbListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get snapshot of current boss attributes
                val healthSnapshot = snapshot.child("boss").child("health")
                val attackSnapshot = snapshot.child("boss").child("attack")
                val levelSnapshot = snapshot.child("boss").child("level")

                // Cast values appropriately
                var longHealth = healthSnapshot.getValue() as Long
                var longAttack = attackSnapshot.getValue() as Long
                var longLevel = levelSnapshot.getValue() as Long

                // Set boss attributes
                health = longHealth.toDouble()
                attack = longAttack.toDouble()
                lvl = longLevel.toInt()
            }

            // Print if error occurs
            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }

        // Grab values with single value event listener
        dbRef.child(androidId).addListenerForSingleValueEvent(fbListener)
    }

    fun takeDamage(damage: Double){
        health = maxOf(health - damage, 0.0)
    }

    fun isDead() : Boolean{
        return health == 0.0
    }

    fun reset(userWon:Boolean) {
        if (userWon) lvl++
        health = baseHealth + healthInc * (lvl - 1)
        attack = baseAttack + attackInc * (lvl - 1)
    }

    fun getAttack():Double {
        return attack
    }
}