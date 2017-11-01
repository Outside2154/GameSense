package edu.outside2154.gamesense

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val latest = ExtraSensory(applicationContext).users?.first()?.files?.first()?.prediction
        val hw = findViewById<TextView>(R.id.hello_world)
        hw.text = latest.toString()
    }
}

