package id.ac.esaunggul.breastcancerdetection.util.extensions

import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis

/**
 * Helper extensions for setting up the start of shared axis transition.
 * @see <a href="https://material.io/develop/android/theming/motion/">Material Design Documentation</a> for more information about this transition.
 */
fun Fragment.startSharedAxisTransition() {
    val backward = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    reenterTransition = backward

    val forward = MaterialSharedAxis(MaterialSharedAxis.Z, true)
    exitTransition = forward
}

/**
 * Helper extensions for setting up the end of shared axis transition.
 * @see <a href="https://material.io/develop/android/theming/motion/">Material Design Documentation</a> for more information about this transition.
 */
fun Fragment.endSharedAxisTransition() {
    val forward = MaterialSharedAxis(MaterialSharedAxis.Z, true)
    enterTransition = forward

    val backward = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    returnTransition = backward
}