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

package id.ac.esaunggul.breastcancerdetection.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.userProfileChangeRequest
import id.ac.esaunggul.breastcancerdetection.di.auth.AuthScope
import id.ac.esaunggul.breastcancerdetection.util.state.AuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AuthScope
class AuthRepo
@Inject
constructor(
    private val auth: FirebaseAuth
) {

    fun checkSession(): AuthState {
        return when (auth.currentUser) {
            null -> AuthState.UNAUTHENTICATED
            else -> AuthState.AUTHENTICATED
        }
    }

    fun login(email: String?, password: String?) = flow {
        emit(AuthState.LOADING)

        if (email != null && password != null) {
            auth.signInWithEmailAndPassword(email, password).await()
            emit(AuthState.AUTHENTICATED)
        } else {
            emit(AuthState.INVALID)
            delay(64) // Wait for a bit so livedata can react accordingly
            emit(AuthState.UNAUTHENTICATED)
        }
    }.catch { e ->
        when (e) {
            is FirebaseAuthEmailException -> {
                emit(AuthState.INVALID)
                delay(64) // Wait for a bit so livedata can react accordingly
                emit(AuthState.UNAUTHENTICATED)
            }
            is FirebaseAuthInvalidCredentialsException -> {
                emit(AuthState.INVALID)
                delay(64) // Wait for a bit so livedata can react accordingly
                emit(AuthState.UNAUTHENTICATED)
            }
            is FirebaseAuthInvalidUserException -> {
                emit(AuthState.INVALID)
                delay(64) // Wait for a bit so livedata can react accordingly
                emit(AuthState.UNAUTHENTICATED)
            }
            is FirebaseAuthWeakPasswordException -> {
                emit(AuthState.WEAK)
                delay(64) // Wait for a bit so livedata can react accordingly
                emit(AuthState.UNAUTHENTICATED)
            }
            else -> {
                emit(AuthState.ERROR)
                delay(64) // Wait for a bit so livedata can react accordingly
                emit(AuthState.UNAUTHENTICATED)
            }
        }
    }.flowOn(Dispatchers.IO)

    fun register(name: String?, email: String?, password: String?) = flow {
        emit(AuthState.LOADING)

        if (email != null && password != null) {
            auth.createUserWithEmailAndPassword(email, password).await()
        }
        auth.currentUser?.updateProfile(userProfileChangeRequest {
            displayName = name
        })

        emit(AuthState.AUTHENTICATED)
    }.catch { e ->
        when (e) {
            is FirebaseAuthUserCollisionException -> {
                emit(AuthState.COLLIDE)
                delay(64) // Wait for a bit so livedata can react accordingly
                emit(AuthState.UNAUTHENTICATED)
            }
            is FirebaseAuthWeakPasswordException -> {
                emit(AuthState.WEAK)
                delay(64) // Wait for a bit so livedata can react accordingly
                emit(AuthState.UNAUTHENTICATED)
            }
            else -> {
                emit(AuthState.ERROR)
                delay(64) // Wait for a bit so livedata can react accordingly
                emit(AuthState.UNAUTHENTICATED)
            }
        }
    }.flowOn(Dispatchers.IO)
}