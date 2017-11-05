package edu.outside2154.gamesense

import android.content.Context
import android.os.Environment
import org.json.JSONObject
import java.io.File
import java.util.*

const val ES_PACKAGE_NAME = "edu.ucsd.calab.extrasensory"
const val FILE_PREFIX_UUID_DIR = "extrasensory.labels."
const val FILE_SUFFIX_SERVER_PREDICTIONS = ".server_predictions.json"
const val FILE_SUFFIX_USER_REPORTED_LABELS = ".user_reported_labels.json"
const val JSON_FIELD_LABEL_NAMES = "label_names"
const val JSON_FIELD_LABEL_PROBABILITIES = "label_probs"
const val JSON_FIELD_LOCATION_COORDINATES = "location_lat_long"

/**
 * Manages ExtraSensory resources.
 * @param ctx The return value of [Context.getApplicationContext].
 */
class ExtraSensory(private val ctx: Context) {
    private val esCtx = ctx.createPackageContext(ES_PACKAGE_NAME, 0)

    /**
     * The ExtraSensory directory.
     */
    private val directory: File? by lazy {
        val esDir = esCtx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (esDir.exists()) esDir else null
    }

    /**
     * All the available [ExtraSensoryUser]s.
     */
    val users : List<ExtraSensoryUser>?
        get() = directory?.list { _, s ->
            s.startsWith(FILE_PREFIX_UUID_DIR)
        }?.map {
            ExtraSensoryUser(File(directory, it), it.removePrefix(FILE_PREFIX_UUID_DIR))
        }
}

/**
 * Corresponds to a user of ExtraSensory.
 * @param directory The directory of the corresponding user.
 * @param uuid The UUID of the corresponding user.
 */
class ExtraSensoryUser constructor(private val directory: File, val uuid: String) {

    /**
     * [ExtraSensoryFile]s for all recorded data for an [ExtraSensoryUser].
     * Includes both server predictions and user-reported labels.
     */
    val files: List<ExtraSensoryFile>
        get() = directory.list { _, s ->
            s.endsWith(FILE_SUFFIX_SERVER_PREDICTIONS) ||
                    s.endsWith(FILE_SUFFIX_USER_REPORTED_LABELS)
        }.map {
            ExtraSensoryFile(
                    File(directory, it),
                    Date(it.slice(0..9).toLong() * 1000),
                    it.endsWith(FILE_SUFFIX_SERVER_PREDICTIONS))
        }
}

/**
 * Holds ExtraSensory reading file information.
 * @property file The associated [File].
 * @property creationTime A [Date] object representing the time the file was created.
 * @property isServer If true, is a server info. Otherwise, is a user reported label.
 */
class ExtraSensoryFile constructor(private val file: File,
                                   val creationTime: Date,
                                   val isServer: Boolean) {

    /**
     * The associated [ExtraSensoryInfo].
     */
    val info: ExtraSensoryInfo?
        get() {
            val json = JSONObject(file.readText())
            val jsonLabels = json.getJSONArray(JSON_FIELD_LABEL_NAMES) ?: return null
            val jsonProbs = json.getJSONArray(JSON_FIELD_LABEL_PROBABILITIES) ?: return null
            val jsonLoc = json.getJSONArray(JSON_FIELD_LOCATION_COORDINATES) ?: return null

            // Sanity check the JSON.
            if (jsonLabels.length() != jsonProbs.length()) return null
            if (jsonLoc.length() != 2) return null

            val preds = (0 until jsonLabels.length()).map {
                jsonLabels.getString(it) to jsonProbs.getDouble(it)
            }.toMap()
            val loc = Pair(jsonLoc.getDouble(0), jsonLoc.getDouble(1))
            return ExtraSensoryInfo(creationTime, preds, loc)
        }
}

/**
 * Holds information for an ExtraSensory file.
 * @property creationTime A [Date] object representing the time the file for this info was created.
 * @property predictions A mapping of labels to confidence level.
 * @property location A latitude/longitude pair.
 */
data class ExtraSensoryInfo constructor(
        val creationTime: Date,
        val predictions: Map<String, Double>,
        val location: Pair<Double, Double>) {
    val topPrediction: String?
        get() = predictions.maxBy { it.value }?.key
}