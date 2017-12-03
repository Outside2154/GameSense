package edu.outside2154.gamesense.model

import edu.outside2154.gamesense.database.BoundFirebaseProperty
import edu.outside2154.gamesense.database.FirebaseRefSnap
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

const val BOSS_BASE_HEALTH = 300.0
const val BOSS_HEALTH_INC = 50.0
const val BOSS_BASE_ATTACK = 20.0
const val BOSS_ATTACK_INC = 5.0

interface Boss : Serializable {
    val health: Double
    val attack: Double
    val level: Int
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
    abstract override var level: Int

    override fun takeDamage(damage: Double) {
        health = maxOf(health - damage, 0.0)
    }

    override fun reset(userWon: Boolean) {
        if (userWon) level++
        health = BOSS_BASE_HEALTH + BOSS_HEALTH_INC * (level - 1)
        attack = BOSS_BASE_ATTACK + BOSS_ATTACK_INC * (level - 1)
    }
}

class BossLocalImpl(override var health: Double,
                    override var attack: Double,
                    override var level: Int) : BossBaseImpl()

class BossFirebaseImpl(root: FirebaseRefSnap) : BossBaseImpl() {
    override var health: Double by BoundFirebaseProperty(root, BOSS_BASE_HEALTH)
    override var attack: Double by BoundFirebaseProperty(root, BOSS_BASE_ATTACK)
    override var level: Int by BoundFirebaseProperty(root, 0)

    private fun writeObject(s: ObjectOutputStream) = s.run {
        writeDouble(health)
        writeDouble(attack)
        writeInt(level)
    }

    private fun readObject(s: ObjectInputStream) = s.run {
        health = readDouble()
        attack = readDouble()
        level = readInt()
    }
}
