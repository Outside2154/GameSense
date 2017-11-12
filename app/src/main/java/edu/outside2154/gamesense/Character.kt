package edu.outside2154.gamesense

import java.util.*

const val USER_BASE_HEALTH = 100.0
const val USER_BASE_ATTACK = 100.0
const val USER_CRIT_MULT = 2.0


class Character (_intStat: Stat, _atkStat: Stat, _regenStat: Stat) {
    var health = USER_BASE_HEALTH
        private set
    var intStat = _intStat
        private set
    var atkStat = _atkStat
        private set
    var regenStat = _regenStat
        private set
    var currency = 0.0
        private set
    val dead
        get() = health == 0.0

    private fun isCritical(): Boolean{
        return Random().nextDouble() < intStat.calcStat() ?: 0.0
    }

    private fun takeDamage(damage: Double){
        health = maxOf(health - damage, 0.0)
    }

    fun fightWith(boss: Boss){
        if (boss.dead) return

        // User attacks
        val attack = atkStat.calcStat() ?: 0.0
        var damage = attack * USER_BASE_ATTACK
        if (isCritical()) damage *= USER_CRIT_MULT
        boss.takeDamage(damage)

        // Boss attacks
        if (boss.dead) return
        takeDamage(boss.attack)
    }
}
