package edu.outside2154.gamesense

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.*

private const val ES_USER = "6DF0201C"

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
        assertEquals("1509321843", file.timestamp)
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