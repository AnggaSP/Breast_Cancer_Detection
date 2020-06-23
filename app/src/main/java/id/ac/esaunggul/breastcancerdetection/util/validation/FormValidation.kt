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

package id.ac.esaunggul.breastcancerdetection.util.validation

/**
 * A singleton class that implement a set of common form validation.
 */
object FormValidation {

    /**
     * Check if the email being inputted is valid or not.
     * @param email String of the email being validated.
     */
    fun isEmailNotValid(email: String): Boolean {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Check if the password being inputted is too weak or not.
     * @param password String of the password being validated.
     */
    fun isPasswordWeak(password: String): Boolean {
        return password.isEmpty() && password.length > 5
    }

    /**
     * Check if the name being inputted is valid or not.
     * @param name String of the name being validated.
     */
    fun isNameNotValid(name: String): Boolean {
        return !name.matches(nameRegex)
    }

    private val nameRegex: Regex =
        "^(?:[\\p{L}\\p{Mn}\\p{Pd}'\\x{2019}]+\\s[\\p{L}\\p{Mn}\\p{Pd}'\\x{2019}]+\\s?)+\$".toRegex()
}