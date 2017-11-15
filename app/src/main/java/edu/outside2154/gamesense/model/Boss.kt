package edu.outside2154.gamesense.model

import java.io.Serializable

const val BOSS_BASE_HEALTH = 100.0
const val BOSS_HEALTH_INC = 50.0
const val BOSS_BASE_ATTACK = 20.0
const val BOSS_ATTACK_INC = 5.0

class Boss(health : Double, attack : Double, level : Int) : Serializable {
    var health = health
        private set
    var attack = attack
        private set
    var lvl = level
        private set
    val dead
        get() = health == 0.0

    fun takeDamage(damage: Double){
        health = maxOf(health - damage, 0.0)
    }

    fun reset(userWon: Boolean) {
        if (userWon) lvl++
        health = BOSS_BASE_HEALTH + BOSS_HEALTH_INC * (lvl - 1)
        attack = BOSS_BASE_ATTACK + BOSS_ATTACK_INC * (lvl - 1)
    }
}
