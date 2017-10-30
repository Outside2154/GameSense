package edu.outside2154.gamesense

import android.content.Context
import android.os.Environment
import android.util.Pair

import org.json.JSONObject

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.ArrayList
import java.util.TreeSet

private val SERVER_PREDICTIONS_FILE_SUFFIX = ".server_predictions.json"
private val USER_REPORTED_LABELS_FILE_SUFFIX = ".user_reported_labels.json"

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
    // Locate the ESA saved files directory, and the specific minute-example's file:
    val extraSensoryAppContext = ctx.createPackageContext("edu.ucsd.calab.extrasensory", 0)
    val esaFilesDir = File(extraSensoryAppContext.getExternalFilesDir(
            Environment.DIRECTORY_DOCUMENTS),
            "extrasensory.labels." + uuidPrefix)
    if (esaFilesDir.exists()) return esaFilesDir
    return null
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
    val esaFilesDir = getUserFilesDirectory(ctx, uuidPrefix) ?: return null
    val filenames = esaFilesDir.list { _, s ->
        s.endsWith(SERVER_PREDICTIONS_FILE_SUFFIX)
                || s.endsWith(USER_REPORTED_LABELS_FILE_SUFFIX) }
    val userTimestampsSet = TreeSet<String>()
    for (filename in filenames) {
        val timestamp = filename.substring(0, 10) // The timestamps always occupy 10 characters
        userTimestampsSet.add(timestamp)
    }
    return ArrayList(userTimestampsSet)
}

/**
 * Read text from the label file saved by ExtraSensory App, for a particualr minute-example.
 * @param ctx The return value of getApplicationContext()
 * @param uuidPrefix The prefix (8 characters) of the user's UUID
 * @param timestamp The timestamp of the desired minute example
 * @param serverOrUser Read the server-predictions if true, and the user-reported labels if false
 * @return The text inside the file, or null if had trouble finding or reading the file
 */
fun readESALabelsFileForMinute(ctx: Context, uuidPrefix: String, timestamp: String,
                                       serverOrUser: Boolean): String? {
    val esaFilesDir = getUserFilesDirectory(ctx, uuidPrefix) ?: return null

    val fileSuffix = if (serverOrUser) SERVER_PREDICTIONS_FILE_SUFFIX
    else USER_REPORTED_LABELS_FILE_SUFFIX

    val minuteLabelsFile = File(esaFilesDir, timestamp + fileSuffix)

    // Read the file:
    val text = StringBuilder()
    val bufferedReader = BufferedReader(FileReader(minuteLabelsFile))
    do {
        val line = bufferedReader.readLine()
        text.append(line)
        text.append('\n')
    } while (line != null)
    bufferedReader.close()
    return text.toString()
}

/**
 * Prse the content of a minute's server-prediction file to extract the labels and probabilities assigned to the labels.
 * @param predictionFileContent The content of a specific minute server-prediction file
 * @return List of label name and probability pairs, or null if had trouble.
 */
fun parseServerPredictionLabelProbabilities(predictionFileContent: String):
        List<Pair<String, Double>>? {
    val jsonObject = JSONObject(predictionFileContent)
    val labelArray = jsonObject.getJSONArray(JSON_FIELD_LABEL_NAMES)
    val probArray = jsonObject.getJSONArray(JSON_FIELD_LABEL_PROBABILITIES)
    // Make sure both arrays have the same size:
    if (labelArray == null || probArray == null || labelArray.length() != probArray.length()) {
        return null
    }
    val labelsAndProbabilities = ArrayList<Pair<String, Double>>(labelArray.length())
    for (i in 0 until labelArray.length()) {
        val label = labelArray.getString(i)
        val prob = probArray.getDouble(i)
        labelsAndProbabilities.add(Pair(label, prob))
    }
    return labelsAndProbabilities
}

/**
 * Parse the content of a minute's server-prediction file to extract the representative location coordinates for that minute.
 * @param predictionFileContent The content of a specific minute server-prediction file
 * @return An array of 2 numbers (or null if had trouble parsing the file or if there were no coordinates available).
 * The numbers are decimal degrees values for latitude and longitude geographic coordinates.
 */
fun parseLocationLatitudeLongitude(predictionFileContent: String): DoubleArray? {
    val jsonObject = JSONObject(predictionFileContent)
    val locationCoordinates = jsonObject.getJSONArray(JSON_FIELD_LOCATION_COORDINATES)
    // Expect this array to have exactly 2 values:
    if (locationCoordinates == null || locationCoordinates.length() != 2) {
        return null
    }
    val latitudeLongitude = DoubleArray(2)
    latitudeLongitude[0] = locationCoordinates.getDouble(0)
    latitudeLongitude[1] = locationCoordinates.getDouble(1)
    return latitudeLongitude
}
