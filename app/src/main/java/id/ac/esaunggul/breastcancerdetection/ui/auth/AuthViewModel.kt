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

package id.ac.esaunggul.breastcancerdetection.ui.auth

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.data.repo.Repo
import id.ac.esaunggul.breastcancerdetection.util.state.AuthState
import id.ac.esaunggul.breastcancerdetection.util.validation.FormValidation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AuthViewModel
@ViewModelInject
constructor(
    private val repo: Repo
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    val nameError = MutableLiveData<Int?>()
    val emailError = MutableLiveData<Int?>()
    val passwordError = MutableLiveData<Int?>()

    val nameField = MutableLiveData<String?>()
    val emailField = MutableLiveData<String?>()
    val passwordField = MutableLiveData<String?>()
    val passwordConfirmField = MutableLiveData<String?>()

    val nameFieldFocus = MutableLiveData<Boolean>(false)
    val emailFieldFocus = MutableLiveData<Boolean>(false)
    val passwordFieldFocus = MutableLiveData<Boolean>(false)
    val passwordConfirmFieldFocus = MutableLiveData<Boolean>(false)

    init {
        _authState.value = repo.checkSession()
    }

    fun login() {
        releaseError()
        releaseFocus()
        if (commonValidation()) {
            viewModelScope.launch(Dispatchers.IO) {
                repo.login(emailField.value, passwordField.value)
                    .collect { state -> _authState.postValue(state) }
            }
        }
    }

    fun register() {
        releaseError()
        releaseFocus()
        when {
            nameField.value.isNullOrEmpty() -> {
                nameError.value = R.string.form_empty
                nameFieldFocus.value = true
            }
            FormValidation.isNameNotValid(nameField.value) -> {
                nameError.value = R.string.name_invalid
                nameFieldFocus.value = true
            }
            !commonValidation() -> Unit
            passwordField.value != passwordConfirmField.value -> {
                passwordError.value = R.string.password_not_matched
                passwordFieldFocus.value = true
            }
            else -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repo.register(nameField.value, emailField.value, passwordField.value)
                        .collect { state -> _authState.postValue(state) }
                }
            }
        }
    }

    fun release() {
        releaseField()
        releaseError()
        releaseFocus()
    }

    private fun commonValidation(): Boolean {
        when {
            emailField.value.isNullOrEmpty() -> {
                emailError.value = R.string.form_empty
                emailFieldFocus.value = true
                return false
            }
            FormValidation.isEmailNotValid(emailField.value) -> {
                emailError.value = R.string.email_invalid
                emailFieldFocus.value = true
                return false
            }
            passwordField.value.isNullOrEmpty() -> {
                passwordError.value = R.string.form_empty
                passwordFieldFocus.value = true
                return false
            }
            FormValidation.isPasswordWeak(passwordField.value) -> {
                passwordError.value = R.string.password_weak
                passwordFieldFocus.value = true
                return false
            }
            else -> return true
        }
    }

    private fun releaseError() {
        nameError.value = null
        emailError.value = null
        passwordError.value = null
    }

    private fun releaseField() {
        nameField.value = null
        emailField.value = null
        passwordField.value = null
        passwordConfirmField.value = null
    }

    private fun releaseFocus() {
        nameFieldFocus.value = false
        emailFieldFocus.value = false
        passwordFieldFocus.value = false
        passwordConfirmFieldFocus.value = false
    }
}