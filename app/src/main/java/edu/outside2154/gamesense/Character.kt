package edu.outside2154.gamesense

import java.util.*

/**
 * Created by Nurbergen on 11/4/17.
 */

class Character (intelligence: Stat, attack: Stat, regeneration: Stat) {
    private val baseHealth = 100.0
    private val baseAttack = 100.0
    private val criticalMultiplier = 2.0

    private var health = baseHealth
    private var intStat = intelligence
    private var atkStat = attack
    private var regenStat = regeneration
    private var currency = 0.0
    private var avatar = ""

    private fun isCritical(): Boolean{
        return Random().nextDouble() < intStat.calcStat() ?: 0.0
    }

    private fun takeDamage(damage: Double){
        health = maxOf(health - damage, 0.0)
    }

    fun fightWith(boss: Boss){
        if (boss.isDead()) return

        // User attacks
        val attack = atkStat.calcStat() ?: 0.0
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
}
