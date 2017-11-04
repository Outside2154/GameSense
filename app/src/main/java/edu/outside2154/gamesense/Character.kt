package edu.outside2154.gamesense

import java.util.*
import edu.outside2154.gamesense.StatType.*

/**
 * Created by iammichelleau on 11/4/17.
 */

class Character () {
    private val BASE_HEALTH: Double = 100.0
    private val BASE_ATK: Double = 100.0
    private val CRIT_MULTIPLIER: Double = 1.0

    private var health: Double
    private var regenStat: Stat?
    private var intStat: Stat?
    private var atkStat: Stat?
    private var avatar: String
    private var money: Double


    init {
        this.health = BASE_HEALTH
        regenStat = null
        intStat = null
        atkStat = null
        this.avatar = ""
        this.money = 10000000.0
    }

    public fun setHealthRegen(statType: StatType, stat: Map<String, Double>) {
        when(statType) {
            HEALTH -> regenStat = Stat(stat)
            INT -> intStat = Stat(stat)
            ATK -> atkStat = Stat(stat)
        }

    }

    public fun calcCrit(): Boolean {
        val random = Random()

        return random.nextDouble() < intStat?.calcStat() ?: 1.0
    }

    public fun battle(boss: Boss) {
        if (boss.isDead())
            return

        var damage = (atkStat?.calcStat() ?: 0.0) * BASE_ATK
        if (calcCrit()) {
            damage *= CRIT_MULTIPLIER
        }
        boss.takeDamage(damage)

        if (boss.isDead())
            return

    }

    public fun takeDamage(damage: Double) {
        this.health -= maxOf(this.health - damage, 0.0)
    }

    public fun isDead(): Boolean {
        return this.health.equals(0.0)
    }

}