package id.ac.esaunggul.breastcancerdetection.util.extensions

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding

/**
 * Apply window insets to the top and bottom padding using [ViewCompat] for [View]
 * that are not edge-to-edge aware.
 *
 * Do note that this will consume the padding set in that view.
 * @param view View apply insets to that view.
 */
fun applyInsets(view: View) {
    ViewCompat.setOnApplyWindowInsetsListener(view, null)
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        v.updatePadding(top = insets.systemWindowInsets.top)
        v.updatePadding(bottom = insets.systemWindowInsets.bottom)
        insets
    }
}

/**
 * Apply window insets to the top and bottom padding using [ViewCompat] for [View]
 * that are not edge-to-edge aware.
 *
 * Do note that this will consume the padding set in that view.
 * @param view View apply insets to that view.
 */
fun removeInsets(view: View) {
    ViewCompat.setOnApplyWindowInsetsListener(view, null)
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        v.updatePadding(top = 0)
        v.updatePadding(bottom = 0)
        insets
    }
}