package edu.outside2154.gamesense.model

import edu.outside2154.gamesense.database.BoundFirebaseProperty
import edu.outside2154.gamesense.database.FirebaseRefSnap
import edu.outside2154.gamesense.database.SelfBoundFirebaseProperty
import java.io.Serializable
import java.util.*

const val PLAYER_BASE_HEALTH = 100.0
const val PLAYER_BASE_ATTACK = 100.0
const val PLAYER_CRIT_MULT = 2.0

interface Player : Serializable {
    val regenStat: Stat
    val atkStat: Stat
    val intStat: Stat
    val health: Double
    val currency: Int

    val pureDamage: Double
        get() = PLAYER_BASE_ATTACK * (atkStat.calcStat() ?: 0.0)
    val dead
        get() = health == 0.0

    /**
     * Handles player criticals via intStat value.
     *
     * @return Boolean if attack was critical based on intStat.
     */
    fun isCritical(): Boolean

    /**
     * Handles health reduction during fight with boss.
     */
    fun takeDamage(damage: Double)

    /**
     * Handles player and boss interaction during fight.
     */
    fun fight(boss: Boss)
}

abstract class PlayerBaseImpl : Player {
    abstract override var regenStat: Stat
    abstract override var atkStat: Stat
    abstract override var intStat: Stat
    abstract override var health: Double
    abstract override var currency: Int

    override fun isCritical(): Boolean {
        val rand = Random()
        return rand.nextDouble() < intStat.calcStat() ?: 0.0
    }

    override fun takeDamage(damage: Double) {
        health = maxOf(health - damage, 0.0)
    }

    override fun fight(boss: Boss) {
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

class PlayerLocalImpl(override var regenStat: Stat,
                      override var atkStat: Stat,
                      override var intStat: Stat,
                      override var health: Double,
                      override var currency: Int) : PlayerBaseImpl()

class PlayerFirebaseImpl(root: FirebaseRefSnap) : PlayerBaseImpl() {
    override var regenStat: Stat by SelfBoundFirebaseProperty(root, Stat(mapOf(), mapOf()))
    override var atkStat: Stat by SelfBoundFirebaseProperty(root, Stat(mapOf(), mapOf()))
    override var intStat: Stat by SelfBoundFirebaseProperty(root, Stat(mapOf(), mapOf()))
    override var health: Double by BoundFirebaseProperty(root, PLAYER_BASE_HEALTH)
    override var currency: Int by BoundFirebaseProperty(root, 0)
}
