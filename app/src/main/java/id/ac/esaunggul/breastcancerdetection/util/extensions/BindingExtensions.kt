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

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Lazily inflates a binding layout using [DataBindingUtil] and returns the newly-created
 * binding for that layout.
 */
@MainThread
inline fun <reified T : ViewDataBinding> binds(
    @NonNull inflater: LayoutInflater,
    @LayoutRes resId: Int,
    container: ViewGroup?
): Lazy<T> = lazy { DataBindingUtil.inflate<T>(inflater, resId, container, false) }

/**
 * Lazily set the Activity's content view to the given layout and return the associated binding.
 * The given layout resource must not be a merge layout.
 */
@MainThread
inline fun <reified T : ViewDataBinding> binds(
    @NonNull activity: Activity,
    @LayoutRes resId: Int
): Lazy<T> = lazy { DataBindingUtil.setContentView<T>(activity, resId) }