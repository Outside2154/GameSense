package edu.outside2154.gamesense.model

import java.util.*

const val PLAYER_BASE_HEALTH = 100.0
const val PLAYER_BASE_ATTACK = 100.0
const val PLAYER_CRIT_MULT = 2.0


class Player(_intStat: Stat, _atkStat: Stat, _regenStat: Stat) {
    var health = PLAYER_BASE_HEALTH
        private set
    var intStat = _intStat
        private set
    var atkStat = _atkStat
        private set
    var regenStat = _regenStat
        private set
    var currency = 0.0
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
