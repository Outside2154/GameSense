package edu.outside2154.gamesense.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Space
import android.widget.TextView

import edu.outside2154.gamesense.R
import edu.outside2154.gamesense.dpToPx
import edu.outside2154.gamesense.runAndRecycle

const val LABELED_BAR_VIEW_PADDING = 10.0

/**
 * A progress bar with a label on the left or right.
 */
class LabeledBarView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {
    // Private view variables.
    private val vText = TextView(context)
    private val vBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)

    // Public attributes.
    var label
        get() = vText.text
        set(value) { vText.text = value }
    var progress
        get() = vBar.progress
        set(value) { vBar.progress = value }

    init {
        // Configure bar elements.
        vText.layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT)
        val space = Space(context).apply {
            layoutParams = LayoutParams(
                    dpToPx(context, LABELED_BAR_VIEW_PADDING), LayoutParams.MATCH_PARENT)
        }
        vBar.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        // Apply XML attributes.
        context.theme.obtainStyledAttributes(
                attrs, R.styleable.LabeledBarView, defStyle, 0).runAndRecycle {
            label = getString(R.styleable.LabeledBarView_label)
            vText.layoutParams.width = getDimensionPixelSize(
                    R.styleable.LabeledBarView_label_width, LayoutParams.WRAP_CONTENT)
        }

        // Add bar elements.
        addView(vText)
        addView(space)
        addView(vBar)
    }
}
