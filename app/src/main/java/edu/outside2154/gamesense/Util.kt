package edu.outside2154.gamesense

import android.content.Context
import android.content.res.TypedArray
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.TypedValue

inline fun FragmentManager.transact(func: FragmentTransaction.() -> FragmentTransaction) =
        beginTransaction().func().commit()

inline fun TypedArray.runAndRecycle(func: TypedArray.() -> Unit) =
        try { func() } finally { recycle() }

fun dpToPx(context: Context, px: Double): Int =
        TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, px.toFloat(),
                context.resources.displayMetrics).toInt()
