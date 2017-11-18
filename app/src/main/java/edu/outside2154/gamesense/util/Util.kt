package edu.outside2154.gamesense.util

import android.content.res.TypedArray
import android.os.Build
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.TypedValue
import android.content.Context
import android.provider.Settings.Secure

/**
 * Runs [func] with a [FragmentManager] as receiver, automatically handling
 * [FragmentManager.beginTransaction] and [FragmentTransaction.commit].
 */
inline fun FragmentManager.transact(func: FragmentTransaction.() -> FragmentTransaction) =
        beginTransaction().func().commit()

/**
 * Runs [func] with a [TypedArray] as receiver, automatically handling [TypedArray.recycle].
 */
inline fun TypedArray.runAndRecycle(func: TypedArray.() -> Unit) =
        try { func() } finally { recycle() }

/**
 * Converts [dp] from display units to pixels.
 */
fun dpToPx(context: Context, dp: Double): Int =
        TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
                context.resources.displayMetrics).toInt()

/**
 * Returns Android ID of current device
 *
 * @param Takes context in to determine ID
 * @return Returns default Android ID if an emulator is being used, or the actual Android device ID
 */
fun getAndroidId(context: Context): String =
    if (isEmulator()) "1cf08e3503018df0" else Secure.getString(context.contentResolver, Secure.ANDROID_ID)

/**
 * Handles determining if currently running device is an emulator for testing purposes
 *
 * @return Boolean of whether or not the current running device is an emulator
 */
fun isEmulator(): Boolean {
    return (Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
            || "google_sdk" == Build.PRODUCT)
}
