package edu.outside2154.gamesense.model

import android.util.Log
import java.io.Serializable
import java.util.*

const val PLAYER_BASE_HEALTH = 100.0
const val PLAYER_BASE_ATTACK = 100.0
const val PLAYER_CRIT_MULT = 2.0

class Player(regen : Stat, attack : Stat, intelligence : Stat, health : Double, currency: Double) : Serializable {
    var regenStat = regen
        private set
    var atkStat = attack
        private set
    var intStat = intelligence
        private set
    var health = health
        private set
    var currency = currency
        private set
    val pureDamage
        get() = PLAYER_BASE_ATTACK * (atkStat.calcStat() ?: 0.0)
    val dead
        get() = health == 0.0
    private val rand = Random()

    private fun isCritical(): Boolean{
        return rand.nextDouble() < intStat.calcStat() ?: 0.0
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
