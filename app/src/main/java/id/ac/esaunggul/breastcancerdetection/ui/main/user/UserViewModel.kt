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

package id.ac.esaunggul.breastcancerdetection.ui.main.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.data.model.UserModel
import id.ac.esaunggul.breastcancerdetection.data.repo.MainRepo
import id.ac.esaunggul.breastcancerdetection.di.main.MainScope
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import id.ac.esaunggul.breastcancerdetection.util.validation.FormValidation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@MainScope
class UserViewModel
@Inject
constructor(
    private val mainRepo: MainRepo
) : ViewModel() {

    private val _profile = MutableLiveData<UserModel>()
    val profile: LiveData<UserModel> = _profile

    private val _state = MutableLiveData<ResourceState<out Nothing?>?>()
    val state: LiveData<ResourceState<out Nothing?>?> = _state

    val nameError = MutableLiveData<Int?>()
    val addressError = MutableLiveData<Int?>()
    val historyError = MutableLiveData<Int?>()
    val dateError = MutableLiveData<Int?>()
    val concernError = MutableLiveData<Int?>()

    val nameField = MutableLiveData<String?>()
    val addressField = MutableLiveData<String?>()
    val historyField = MutableLiveData<String?>()
    val dateField = MutableLiveData<String?>()
    val concernField = MutableLiveData<String?>()

    val nameFieldFocus = MutableLiveData<Boolean>(false)
    val addressFieldFocus = MutableLiveData<Boolean>(false)
    val historyFieldFocus = MutableLiveData<Boolean>(false)
    val dateFieldFocus = MutableLiveData<Boolean>(false)
    val concernFieldFocus = MutableLiveData<Boolean>(false)

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepo.fetchUserData().collect { state ->
                when (state) {
                    is ResourceState.Success -> state.data?.let { data -> _profile.postValue(data) }
                    is ResourceState.Error -> {
                        Timber.e("Cannot fetch userdata, using default name.")
                        Timber.e("Reason: ${state.code}")
                        _profile.postValue(UserModel("ERROR", "User", "user@error.com"))
                    }
                    is ResourceState.Loading -> Timber.d("Fetching the userdata")
                }
            }
        }
    }

    fun saveConsultInfo() {
        releaseError()
        releaseFocus()
        when {
            !commonValidation() -> Unit
            concernField.value.isNullOrEmpty() -> {
                concernError.value = R.string.form_empty
                concernFieldFocus.value = true
            }
            else -> {
                viewModelScope.launch(Dispatchers.IO) {
                    mainRepo.saveConsultationInformation(
                        nameField.value,
                        addressField.value,
                        historyField.value,
                        dateField.value,
                        concernField.value
                    ).collect { state ->
                        _state.postValue(state)
                    }
                }
            }
        }
    }

    fun saveDiagnosisInfo() {
        releaseError()
        releaseFocus()
        if (commonValidation()) {
            viewModelScope.launch(Dispatchers.IO) {
                mainRepo.saveDiagnosisInformation(
                    nameField.value,
                    addressField.value,
                    historyField.value,
                    dateField.value
                ).collect { state ->
                    _state.postValue(state)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepo.logout().collect { state ->
                _state.postValue(state)
            }
        }
    }

    fun release() {
        releaseField()
        releaseError()
        releaseFocus()
        _state.value = null
    }

    private fun commonValidation(): Boolean {
        when {
            FormValidation.isNameNotValid(nameField.value) -> {
                nameError.value = R.string.name_invalid
                nameFieldFocus.value = true
                return false
            }
            addressField.value.isNullOrEmpty() -> {
                addressError.value = R.string.form_empty
                addressFieldFocus.value = true
                return false
            }
            historyField.value.isNullOrEmpty() -> {
                historyError.value = R.string.form_empty
                historyFieldFocus.value = true
                return false
            }
            dateField.value.isNullOrEmpty() -> {
                dateError.value = R.string.form_empty
                dateFieldFocus.value = true
                return false
            }
            else -> return true
        }
    }

    private fun releaseError() {
        nameError.value = null
        addressError.value = null
        historyError.value = null
        dateError.value = null
        concernError.value = null
    }

    private fun releaseField() {
        nameField.value = null
        addressField.value = null
        historyField.value = null
        dateField.value = null
        concernField.value = null
    }

    private fun releaseFocus() {
        nameFieldFocus.value = false
        addressFieldFocus.value = false
        historyFieldFocus.value = false
        dateFieldFocus.value = false
        concernFieldFocus.value = false
    }
}