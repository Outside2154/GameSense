package edu.outside2154.gamesense

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fileContent = readESALabelsFileForMinute(
                applicationContext,
                "6DF0201C",
                "1509321843",
                true) ?: return
        val labelsAndProbs = parseServerPredictionLabelProbabilities(fileContent) ?: return
        val latLong = parseLocationLatitudeLongitude(fileContent) ?: return
        val pairsStr = "${labelsAndProbs.size} labels:\n" + labelsAndProbs.entries.map {
            "${it.key}: ${it.value}"
        }
        val top = labelsAndProbs.maxBy { it.value }?.key ?: "None"
        val textToPresent = "LatLong: ${latLong.toString()}\n\nServer predictions:\n${pairsStr}\n\nTop: ${top}"
        val hw = findViewById<TextView>(R.id.hello_world)
        hw.setText(textToPresent)
    }
}

