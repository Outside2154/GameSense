package edu.outside2154.gamesense

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

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