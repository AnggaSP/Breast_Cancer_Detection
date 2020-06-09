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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import id.ac.esaunggul.breastcancerdetection.di.auth.AuthScope
import id.ac.esaunggul.breastcancerdetection.util.AuthState
import javax.inject.Inject

@AuthScope
class AuthViewModel
@Inject
constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()

    init {
        when (firebaseAuth.currentUser) {
            null -> _authState.value = AuthState.UNAUTHENTICATED
            else -> _authState.value = AuthState.AUTHENTICATED
        }
    }

    val authState: LiveData<AuthState> = _authState
}