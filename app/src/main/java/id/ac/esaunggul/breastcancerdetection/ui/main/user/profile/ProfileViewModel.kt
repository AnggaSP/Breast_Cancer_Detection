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

package id.ac.esaunggul.breastcancerdetection.ui.main.user.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ac.esaunggul.breastcancerdetection.data.model.UserModel
import id.ac.esaunggul.breastcancerdetection.data.repo.MainRepo
import id.ac.esaunggul.breastcancerdetection.di.main.MainScope
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@MainScope
class ProfileViewModel
@Inject
constructor(
    private val mainRepo: MainRepo
) : ViewModel() {

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    private val _profile = MutableLiveData<UserModel>()
    val profile: LiveData<UserModel> = _profile

    private val _state = MutableLiveData<ResourceState<out Nothing?>>()
    val state: LiveData<ResourceState<out Nothing?>> = _state

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepo.fetchUserData().collect { value ->
                when (value) {
                    is ResourceState.Success -> _profile.postValue(value.data)
                    is ResourceState.Error -> {
                        Log.e(TAG, "Cannot fetch userdata, using default name.")
                        Log.e(TAG, "Reason: ${value.code}")
                        _profile.postValue(UserModel("ERROR", "User", "user@error.com"))
                    }
                    is ResourceState.Loading -> Log.d(TAG, "Fetching the userdata")
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepo.logout().collect { value ->
                _state.postValue(value)
            }
        }
    }
}