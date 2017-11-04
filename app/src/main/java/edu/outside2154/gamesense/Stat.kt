package edu.outside2154.gamesense;

import java.lang.Double.sum

/**
 * Created by iammichelleau on 11/4/17.
 */

data class StatItems(val items: Map<String, Double>) {
    operator fun plus(other: StatItems): StatItems {
        // "Merge" the two maps
        return StatItems(items.mapValues { (k, v) ->
            v + (other.items[k] ?: 0.0)
        })
    }
}

public class Stat (initGoals: Map<String, Double>) {

    var goals = StatItems(mapOf())
    var current = StatItems(mapOf())

    init{
        this.goals = StatItems(initGoals.mapValues{ (k, v) -> v * 60 })
        this.current = StatItems(initGoals.mapValues{ (k, v) -> v * 0 })
    }

    public fun updateCurrent (data: Map<String, Double>) {
        this.current += StatItems(data)
    }

    public fun calcStat (): Double? {
        var divisor = this.goals.items.values.sum()
        if (divisor.equals(0.0)) return null
        return this.current.items.values.sum()/divisor
    }

    public fun reset () {
        this.current = StatItems(current.items.mapValues{ (k, v) -> v * 0 })
    }
}
