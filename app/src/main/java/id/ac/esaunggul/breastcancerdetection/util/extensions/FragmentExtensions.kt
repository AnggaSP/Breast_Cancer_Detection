/*
 * Copyright 2020 Angga Satya Putra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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