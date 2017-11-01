package edu.outside2154.gamesense

import android.content.Context
import android.os.Environment

import org.json.JSONObject

import java.io.File

private val FILE_SUFFIX_SERVER_PREDICTIONS = ".server_predictions.json"
private val FILE_SUFFIX_USER_REPORTED_LABELS = ".user_reported_labels.json"

private val JSON_FIELD_LABEL_NAMES = "label_names"
private val JSON_FIELD_LABEL_PROBABILITIES = "label_probs"
private val JSON_FIELD_LOCATION_COORDINATES = "location_lat_long"

/**
 * Return the directory, where a user's ExtraSensory-App label files should be
 * @param ctx The return value of getApplicationContext()
 * @param uuidPrefix The prefix (8 characters) of the user's UUID
 * @return The user's files' directory
 */
fun getUserFilesDirectory(ctx: Context, uuidPrefix: String): File? {
    val esCtx = ctx.createPackageContext("edu.ucsd.calab.extrasensory", 0)
    val esDir = File(
            esCtx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "extrasensory.labels." + uuidPrefix)
    return if (esDir.exists()) esDir else null
}

/**
 * Get the list of timestamps, for which this user has saved files from ExtraSensory App.
 * @param ctx The return value of getApplicationContext()
 * @param uuidPrefix The prefix (8 characters) of the user's UUID
 * @return List of timestamps (strings), each representing a minute that has a file for this user.
 * The list will be sorted from earliest to latest.
 * In case the user's directory was not found, null will be returned.
 */
fun getTimestampsForUser(ctx: Context, uuidPrefix: String): List<String>? {
    // Filter out so only timestamp files remain, then get the timestamps (chars 0-9).
    return getUserFilesDirectory(ctx, uuidPrefix)?.list { _, s ->
        s.endsWith(FILE_SUFFIX_SERVER_PREDICTIONS) || s.endsWith(FILE_SUFFIX_USER_REPORTED_LABELS)
    }?.map { it.slice(0..9) }
}

/**
 * Read text from the label file saved by ExtraSensory App, for a particualr minute-example.
 * @param ctx The return value of getApplicationContext()
 * @param uuidPrefix The prefix (8 characters) of the user's UUID
 * @param timestamp The timestamp of the desired minute example
 * @param useServerPreds Read the server-predictions if true, and the user-reported labels if false
 * @return The text inside the file, or null if had trouble finding or reading the file
 */
fun readESALabelsFileForMinute(ctx: Context, uuidPrefix: String, timestamp: String,
                               useServerPreds: Boolean): String? {
    val esaFilesDir = getUserFilesDirectory(ctx, uuidPrefix) ?: return null

    val fileSuffix = if (useServerPreds) FILE_SUFFIX_SERVER_PREDICTIONS
    else FILE_SUFFIX_USER_REPORTED_LABELS

    val file = File(esaFilesDir, timestamp + fileSuffix)

    return file.toString()
}

/**
 * Prse the content of a minute's server-prediction file to extract the labels and probabilities
 * assigned to the labels.
 * @param predictionFileContent The content of a specific minute server-prediction file
 * @return List of label name and probability pairs, or null if had trouble.
 */
fun parseServerPredictionLabelProbabilities(predictionFileContent: String): Map<String, Double>? {
    val json = JSONObject(predictionFileContent)
    val labels = json.getJSONArray(JSON_FIELD_LABEL_NAMES) ?: return null
    val probs = json.getJSONArray(JSON_FIELD_LABEL_PROBABILITIES) ?: return null
    if (labels.length() != probs.length()) return null

    return (0 until labels.length()).map {
        Pair(labels.getString(it), probs.getDouble(it))
    }.toMap()
}

/**
 * Parse the content of a minute's server-prediction file to extract the representative location
 * coordinates for that minute.
 * @param predictionFileContent The content of a specific minute server-prediction file
 * @return An array of 2 numbers (or null if had trouble parsing the file or if there were no
 * coordinates available).
 * The numbers are decimal degrees values for latitude and longitude geographic coordinates.
 */
fun parseLocationLatitudeLongitude(predictionFileContent: String): Pair<Double, Double>? {
    val jsonObject = JSONObject(predictionFileContent)
    val loc = jsonObject.getJSONArray(JSON_FIELD_LOCATION_COORDINATES) ?: return null

    // Expect 2 values.
    if (loc.length() != 2) return null

    return Pair(loc.getDouble(0), loc.getDouble(1))
}
