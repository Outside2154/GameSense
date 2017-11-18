package edu.outside2154.gamesense.model

import edu.outside2154.gamesense.database.BoundFirebaseProperty
import edu.outside2154.gamesense.database.FirebaseRefSnap
import java.io.Serializable

const val BOSS_BASE_HEALTH = 100.0
const val BOSS_HEALTH_INC = 50.0
const val BOSS_BASE_ATTACK = 20.0
const val BOSS_ATTACK_INC = 5.0

interface Boss : Serializable {
    val health: Double
    val attack: Double
    val lvl: Int
    val dead
        get() = health == 0.0

    /**
     * Handles health reduction during fight with player.
     */
    fun takeDamage(damage: Double)

    /**
     * Resets boss at the end of the week based on remaining health.
     */
    fun reset(userWon: Boolean)
}

abstract class BossBaseImpl : Boss {
    abstract override var health: Double
    abstract override var attack: Double
    abstract override var lvl: Int

    override fun takeDamage(damage: Double) {
        health = maxOf(health - damage, 0.0)
    }

    override fun reset(userWon: Boolean) {
        if (userWon) lvl++
        health = BOSS_BASE_HEALTH + BOSS_HEALTH_INC * (lvl - 1)
        attack = BOSS_BASE_ATTACK + BOSS_ATTACK_INC * (lvl - 1)
    }
}

class BossLocalImpl(override var health: Double,
                    override var attack: Double,
                    override var lvl: Int) : BossBaseImpl()

class BossFirebaseImpl(root: FirebaseRefSnap) : BossBaseImpl() {
    override var health: Double by BoundFirebaseProperty(root, BOSS_BASE_HEALTH)
    override var attack: Double by BoundFirebaseProperty(root, BOSS_BASE_ATTACK)
    override var lvl: Int by BoundFirebaseProperty(root, 0)
}