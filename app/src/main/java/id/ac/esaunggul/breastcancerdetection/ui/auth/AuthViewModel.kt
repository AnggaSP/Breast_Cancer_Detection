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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.data.repo.Repo
import id.ac.esaunggul.breastcancerdetection.ui.auth.login.LoginFragmentDirections
import id.ac.esaunggul.breastcancerdetection.ui.auth.register.RegisterFragmentDirections
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.NavigationDispatcher
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.ToastDispatcher
import id.ac.esaunggul.breastcancerdetection.util.state.AuthState
import id.ac.esaunggul.breastcancerdetection.util.validation.FormValidation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class AuthViewModel
@ViewModelInject
constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val toastDispatcher: ToastDispatcher,
    private val repo: Repo
) : ViewModel() {

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

    private val _clickable = MutableLiveData<Boolean>(true)
    val clickable = _clickable

    private val _loading = MutableLiveData<Boolean>(false)
    val loading = _loading

    fun init() {
        val state = repo.checkSession()
        if (state == AuthState.AUTHENTICATED) {
            navigationDispatcher.emit { navController ->
                navController.navigate(
                    AuthFragmentDirections.actionUserAuthenticated()
                )
            }
        }
    }

    fun login() {
        releaseError()
        releaseFocus()
        releaseClickableButton()

        if (commonValidation()) {
            viewModelScope.launch(Dispatchers.IO) {
                repo.login(emailField.value, passwordField.value).collect { state ->
                    when (state) {
                        AuthState.AUTHENTICATED ->
                            withContext(Dispatchers.Main) {
                                navigationDispatcher.emit { navController ->
                                    navController.navigate(
                                        LoginFragmentDirections.actionLoginAuthenticated()
                                    )
                                }
                            }

                        AuthState.LOADING -> {
                            Timber.d("Loading the data...")
                            setClickableButtonBackground()
                        }

                        AuthState.INVALID -> {
                            Timber.d("Authentication failed.")
                            emailError.postValue(R.string.login_email_invalid)
                            passwordError.postValue(R.string.login_password_invalid)
                            releaseClickableButtonBackground()
                        }

                        AuthState.ERROR -> {
                            Timber.e("An error has occurred.")
                            toastDispatcher.emit(R.string.network_failed)
                            releaseClickableButtonBackground()
                        }

                        else -> Timber.e("Catastrophic error happened.")
                    }
                }
            }
        }
    }

    fun register() {
        var state = false

        releaseError()
        releaseFocus()
        releaseClickableButton()

        if (nameField.value.isNullOrEmpty()) {
            nameError.value = R.string.form_empty
        } else if (FormValidation.isNameNotValid(nameField.value)) {
            nameError.value = R.string.name_invalid
        }

        state = commonValidation() && state

        if (passwordField.value != passwordConfirmField.value) {
            passwordError.value = R.string.password_not_matched
            state = false
        }

        if (state) {
            viewModelScope.launch(Dispatchers.IO) {
                repo.register(
                    nameField.value,
                    emailField.value,
                    passwordField.value
                ).collect { state ->
                    when (state) {
                        AuthState.AUTHENTICATED ->
                            withContext(Dispatchers.Main) {
                                navigationDispatcher.emit { navController ->
                                    navController.navigate(
                                        RegisterFragmentDirections.actionRegisterAuthenticated()
                                    )
                                }
                            }

                        AuthState.LOADING -> {
                            Timber.d("Loading the data...")
                            setClickableButtonBackground()
                        }

                        AuthState.COLLIDE ->
                            emailError.postValue(R.string.email_exist)

                        AuthState.WEAK -> {
                            Timber.d("Weak password is being passed.")
                            Timber.d("Please check if the validation is doing its job.")
                            passwordError.postValue(R.string.password_weak)
                            releaseClickableButtonBackground()
                        }

                        AuthState.ERROR -> {
                            Timber.e("An error has occurred.")
                            toastDispatcher.emit(R.string.network_failed)
                            releaseClickableButtonBackground()
                        }

                        else -> Timber.e("Catastrophic error happened.")
                    }
                }
            }
        }
    }

    fun release() {
        releaseField()
        releaseError()
        releaseFocus()
        releaseClickableButton()
    }

    private fun commonValidation(): Boolean {
        var state = true
        if (emailField.value.isNullOrEmpty()) {
            emailError.value = R.string.form_empty
            state = false
        } else if (FormValidation.isEmailNotValid(emailField.value)) {
            emailError.value = R.string.email_invalid
            state = false
        }
        if (passwordField.value.isNullOrEmpty()) {
            passwordError.value = R.string.form_empty
            state = false
        } else if (FormValidation.isPasswordWeak(passwordField.value)) {
            passwordError.value = R.string.password_weak
            state = false
        }
        return state
    }

    private fun setClickableButtonBackground() {
        _clickable.postValue(false)
        _loading.postValue(true)
    }

    private fun releaseClickableButton() {
        _loading.value = false
        _clickable.value = true
    }

    private fun releaseClickableButtonBackground() {
        _loading.postValue(false)
        _clickable.postValue(true)
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