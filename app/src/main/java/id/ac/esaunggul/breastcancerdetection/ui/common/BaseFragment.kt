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

package id.ac.esaunggul.breastcancerdetection.ui.common

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.transition.platform.MaterialSharedAxis

/**
 * A class that extends [Fragment] and implement a set of common helper methods.
 */
abstract class BaseFragment : Fragment() {

    /**
     * Lazily inflates a binding layout using [DataBindingUtil] and returns the newly-created
     * binding for that layout.
     */
    @MainThread
    protected inline fun <reified T : ViewDataBinding> binds(
        @LayoutRes resId: Int,
        container: ViewGroup?
    ): Lazy<T> = lazy { DataBindingUtil.inflate<T>(layoutInflater, resId, container, false) }

    /**
     * Apply window insets to the top and bottom padding using [ViewCompat] for [View]
     * that are not edge-to-edge aware.
     *
     * Do note that this will consume the padding set in that view.
     * @param view take [View] object and apply the insets to that view.
     */
    fun applyInsets(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            v.updatePadding(top = insets.systemWindowInsets.top)
            v.updatePadding(bottom = insets.systemWindowInsets.bottom)
            insets
        }
    }

    /**
     * Helper method for setting up the start of shared axis transition.
     * @see <a href="https://material.io/develop/android/theming/motion/">Material Design Documentation</a> for more information about this transition.
     */
    fun startSharedAxisTransition() {
        val backward = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        reenterTransition = backward

        val forward = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = forward
    }

    /**
     * Helper method for setting up the end of shared axis transition.
     * @see <a href="https://material.io/develop/android/theming/motion/">Material Design Documentation</a> for more information about this transition.
     */
    fun endSharedAxisTransition() {
        val forward = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        enterTransition = forward

        val backward = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        returnTransition = backward
    }
}