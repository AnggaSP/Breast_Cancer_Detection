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

package id.ac.esaunggul.breastcancerdetection.util.state

/**
 * An enum class representing the current status of resource data being manipulated and/or fetched.
 */
sealed class ResourceState<T>(
    val data: T? = null,
    val code: String? = null
) {
    class Success<T>(data: T) : ResourceState<T>(data)
    class Loading<T>(data: T? = null) : ResourceState<T>(data)
    class Error<T>(code: String?, data: T? = null) : ResourceState<T>(data, code)
}