package edu.outside2154.gamesense.util

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.*

private const val ES_USER = "6DF0201C"
private const val ES_TIME = "1509321843"

class ExtraSensoryFileTest {
    lateinit var esFile: File

    @Before
    fun setUp() {
        val url = javaClass.getResource(
                "extrasensory.labels.${ES_USER}/${ES_TIME}.server_predictions.json")
        esFile = File(url.toURI())
    }

    @Test
    fun testInfo() {
        val time = Date(ES_TIME.toLong() * 1000)
        val info = ExtraSensoryFile(esFile, time, true).info ?:
                throw AssertionError("Info is null.")
        assertEquals(time, info.creationTime)
        assertEquals(51, info.predictions.size)
        assertEquals(32.8703247, info.location.first, 1e-7)
        assertEquals(-117.2155813, info.location.second, 1e-7)
    }
}

class ExtraSensoryUserTest {
    lateinit var userDir: File

    @Before
    fun setUp() {
        val url = javaClass.getResource("extrasensory.labels.${ES_USER}")
        userDir = File(url.toURI())
    }

    @Test
    fun testFiles() {
        val user = ExtraSensoryUser(userDir, ES_USER)
        val files = user.files
        assertEquals(1, files.size)

        val file = files.first()
        assertEquals(ES_TIME, (file.creationTime.time / 1000).toString())
        assertEquals(true, file.isServer)
    }
}

class ExtraSensoryInfoTest {
    @Test
    fun testTopPrediction() {
        val info = ExtraSensoryInfo(Date(0), mapOf(
                "activity1" to 0.5,
                "activity2" to 0.9,
                "activity3" to 0.7
        ), Pair(1.0, 2.0))
        assertEquals("activity2", info.topPrediction)
    }
}