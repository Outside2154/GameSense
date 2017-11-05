package edu.outside2154.gamesense

/**
 * Created by Nurbergen on 11/4/17.
 */

class Boss {
    private val baseHealth = 100.0
    private val healthInc = 50.0
    private val baseAttack = 20.0
    private val attackInc = 5.0

    private var health = baseHealth
    private var attack = baseAttack
    private var lvl = 1
    private var avatar = ""

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