package edu.outside2154.gamesense;

/**
 * Created by iammichelleau on 11/4/17.
 */

data class StatItems(val items: Map<String, Double>) {
    operator fun plus(other: StatItems): StatItems {
        return StatItems(items.mapValues { (k, v) ->
            v + (other.items[k] ?: 0.0)
        })
    }
}

class Stat (initGoals: Map<String, Double>) {

    var goals = StatItems(mapOf())
    var current = StatItems(mapOf())

    init {
        this.goals = StatItems(initGoals.mapValues { (k, v) -> v * 60.0 })
        this.current = StatItems(initGoals.mapValues { (k, v) -> v * 0.0 })
    }

    fun updateCurrent(data: Map<String, Double>) {
        this.current += StatItems(data)
    }

    fun calcStat(): Double? {
        val divisor = this.goals.items.values.sum()
        if (divisor.equals(0.0)) return null
        return this.current.items.values.sum() / divisor;
    }

    fun reset() {
        this.current = StatItems(this.current.items.mapValues { (k, v) -> v * 0.0 })
    }
}
