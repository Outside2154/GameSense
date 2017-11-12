package edu.outside2154.gamesense

/**
 * Created by Nurbergen on 11/4/17.
 */

class Stat (initGoals: Map<String, Double>) {

    data class StatItems(val items: Map<String, Double>) {
        operator fun plus(other: StatItems): StatItems {
            return StatItems(items.mapValues { (k, v) ->
                v + (other.items[k] ?: 0.0)
            })
        }
    }

    private var goals = StatItems(initGoals.mapValues { (_, v) -> v * 60.0 })
    private var current = StatItems(initGoals.mapValues { (_, v) -> v * 0.0 })

    fun updateCurrent(data: Map<String, Double>) {
        current += StatItems(data.mapValues { (_, v) -> v * 60.0 })
    }

    fun calcStat(): Double? {
        val divisor = goals.items.values.sum()
        if (divisor == 0.0) return null
        return current.items.values.sum() / divisor
    }

    fun reset() {
        current = StatItems(current.items.mapValues { (_, v) -> v * 0.0 })
    }
}
