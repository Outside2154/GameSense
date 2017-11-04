package edu.outside2154.gamesense

import android.content.Context
import android.os.Environment
import org.json.JSONObject
import java.io.File

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
                    it.slice(0..9),
                    it.endsWith(FILE_SUFFIX_SERVER_PREDICTIONS))
        }
}

/**
 * Holds ExtraSensory reading file information.
 * @property timestamp The associated timestamp.
 * @property isServer If true, is a server info. Otherwise, is a user reported label.
 * @property file The associated [File].
 */
class ExtraSensoryFile constructor(private val file: File,
                                            val timestamp: String,
                                            val isServer: Boolean) {

    /**
     * The associated [ExtraSensoryInfo].
     */
    val info: ExtraSensoryInfo?
        get() {
            val json = JSONObject(file.readText())
            val json_labels = json.getJSONArray(JSON_FIELD_LABEL_NAMES) ?: return null
            val json_probs = json.getJSONArray(JSON_FIELD_LABEL_PROBABILITIES) ?: return null
            val json_loc = json.getJSONArray(JSON_FIELD_LOCATION_COORDINATES) ?: return null

            // Sanity check the JSON.
            if (json_labels.length() != json_probs.length()) return null
            if (json_loc.length() != 2) return null

            val preds = (0 until json_labels.length()).map {
                json_labels.getString(it) to json_probs.getDouble(it)
            }.toMap()
            val loc = json_loc.getDouble(0) to json_loc.getDouble(1)
            return ExtraSensoryInfo(preds, loc)
        }
}

/**
 * Holds information for an ExtraSensory file.
 * @property predictions A mapping of labels to confidence level.
 * @property location A latitude/longitude pair.
 */
data class ExtraSensoryInfo constructor(
        val predictions: Map<String, Double>,
        val location: Pair<Double, Double>) {
    val topPrediction: String?
        get() = predictions.maxBy { it.value }?.key
}