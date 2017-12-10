package edu.outside2154.gamesense.util

/**
 * Represents an object which requires updating upon some state change.
 */
interface Updatable {
    fun update()
}

/**
 * Turns a double in the range [0.0, 1.0] to an integer in the range [0, 100].
 * @return The converted double if the value is in range, otherwise null.
 */
fun Double.toIntPercent(): Int? = when {
    this < 0.0 -> null
    this > 1.0 -> null
    else -> (this * 100).toInt()
}

/**
 * Turns a double in the range [0.0, 1.0] to an integer in the range [0, 100].
 * @return The converted double if the value is in range, otherwise null.
 */
fun Double.toDoublePercent(): Double? = when {
    this < 0.0 -> null
    this > 1.0 -> null
    else -> (this * 100)
}

/**
 * Tries [f], returning null if an exception is thrown.
 * @param f The function to try.
 * @return The return value of [f] if no exceptions are thrown, null otherwise
 */
fun <T> tryOrNull(f: () -> T): T? {
    return try {
        f()
    } catch (e: Exception) {
        null
    }
}