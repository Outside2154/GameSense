package edu.outside2154.gamesense.model

class Stat (initGoals : Map<String, Double>, currGoals : Map<String, Double>) {

    data class StatItems(val items: Map<String, Double>) {
        operator fun plus(other: StatItems): StatItems {
            return StatItems(items.mapValues { (k, v) ->
                v + (other.items[k] ?: 0.0)
            })
        }
    }

    var goals = StatItems(initGoals)
    var current = StatItems(currGoals)

    fun updateCurrent(data: Map<String, Double>) {
        current += StatItems(data.mapValues { (_, v) -> v * 60.0 })
    }

    fun calcStat(): Double? {
        val divisor = goals.items.values.sum()
        if (divisor == 0.0) return null

        val elements = current.items.mapValues { (k, v) ->
            minOf(v, goals.items[k] ?: 0.0)
        }
        return elements.values.sum() / divisor
    }

    fun reset() {
        current = StatItems(current.items.mapValues { (_, v) -> v * 0.0 })
    }
}
