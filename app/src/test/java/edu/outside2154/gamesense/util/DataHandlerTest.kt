package edu.outside2154.gamesense.util

import org.junit.Assert.*
import org.junit.Test
import java.util.*

private data class Prediction(val creationInt: Int, val prediction: String) {
    val creationTime: Date by lazy { Date(creationInt.toLong()) }
}

class DataHandlerTest {
    private fun userWith(predictions: List<Prediction>): ExtraSensoryUser =
            ExtraSensoryUserTestImpl( "TEST_UUID", predictions.map {
                ExtraSensoryFileTestImpl(
                        it.creationTime, true,
                        ExtraSensoryInfo(
                                it.creationTime,
                                mapOf(it.prediction to 1.0),
                                Pair(0.0, 0.0)))
            })

    @Test
    fun testPullData() {
        // User with predictions from 1 to 10.
        val user = userWith((1..10).map { Prediction(it, it.toString()) })
        val dataHandler = DataHandlerLocalImpl(user, Date(3)) { Date(7) }

        // Expect initial pull to get from 4 to 10
        assertEquals(
                mapOf(*(4..10).map { it.toString() to 1.0 / 60 }.toTypedArray()),
                dataHandler.pullData())

        // Expect next pull to get from 8 to 10
        val fresherData = dataHandler.pullData()
        assertEquals(
                mapOf(*(8..10).map { it.toString() to 1.0 / 60 }.toTypedArray()),
                dataHandler.pullData())
    }
}