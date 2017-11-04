package edu.outside2154.gamesense

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Show the first info of the first user if available.
        val latest = ExtraSensory(applicationContext).users?.first()?.files?.first()?.info
        val hw = findViewById<TextView>(R.id.es_debug)
        hw.text = latest.toString()
    }
}
<<<<<<< Updated upstream

=======
l
>>>>>>> Stashed changes
