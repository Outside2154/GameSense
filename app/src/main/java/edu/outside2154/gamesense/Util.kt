package edu.outside2154.gamesense

import android.content.Context
import android.content.res.TypedArray
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.TypedValue

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
