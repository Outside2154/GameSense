package edu.outside2154.gamesense.model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable

const val BOSS_BASE_HEALTH = 100.0
const val BOSS_HEALTH_INC = 50.0
const val BOSS_BASE_ATTACK = 20.0
const val BOSS_ATTACK_INC = 5.0

class Boss(androidId : String) : Serializable {
    var health = BOSS_BASE_HEALTH
        private set
    var attack = BOSS_BASE_ATTACK
        private set
    var lvl = 1
        private set
    val dead
        get() = health == 0.0

    init {
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
        val dbRef = FirebaseDatabase.getInstance().getReference()
        dbRef.child(androidId).addListenerForSingleValueEvent(fbListener)
    }

    fun takeDamage(damage: Double){
        health = maxOf(health - damage, 0.0)
    }

    fun reset(userWon: Boolean) {
        if (userWon) lvl++
        health = BOSS_BASE_HEALTH + BOSS_HEALTH_INC * (lvl - 1)
        attack = BOSS_BASE_ATTACK + BOSS_ATTACK_INC * (lvl - 1)
    }
}
