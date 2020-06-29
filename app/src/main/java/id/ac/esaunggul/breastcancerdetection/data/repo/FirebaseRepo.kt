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
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.esaunggul.breastcancerdetection.data.model.ArticleModel
import id.ac.esaunggul.breastcancerdetection.data.model.ConsultationFormModel
import id.ac.esaunggul.breastcancerdetection.data.model.DiagnosisFormModel
import id.ac.esaunggul.breastcancerdetection.data.model.UserModel
import id.ac.esaunggul.breastcancerdetection.util.state.AuthState
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepo
@Inject
constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : Repo {

    override fun checkSession(): AuthState {
        return when (auth.currentUser) {
            null -> AuthState.UNAUTHENTICATED
            else -> AuthState.AUTHENTICATED
        }
    }

    override fun fetchArticles(): Flow<ResourceState<List<ArticleModel>>> = flow {
        emit(ResourceState.Loading<List<ArticleModel>>())

        val fetch = database.collection("articles").get().await()

        emit(ResourceState.Success(fetch.toObjects(ArticleModel::class.java)))
    }.catch { error ->
        emit(ResourceState.Error(error.message))
    }.flowOn(Dispatchers.IO)

    override fun fetchUserData(): Flow<ResourceState<UserModel>> = flow {
        emit(ResourceState.Loading<UserModel>())

        val user = auth.currentUser
        user?.let { data ->
            val userData = UserModel(data.uid, data.displayName, data.email, data.photoUrl)
            emit(ResourceState.Success(userData))
        }
    }.catch { error ->
        emit(ResourceState.Error(error.message))
    }.flowOn(Dispatchers.IO)

    override fun login(email: String?, password: String?): Flow<AuthState> = flow {
        emit(AuthState.LOADING)

        if (email != null && password != null) {
            auth.signInWithEmailAndPassword(email, password).await()
            emit(AuthState.AUTHENTICATED)
        } else {
            emit(AuthState.INVALID)
        }
    }.catch { e ->
        when (e) {
            is FirebaseAuthEmailException -> {
                emit(AuthState.INVALID)
            }

            is FirebaseAuthInvalidCredentialsException -> {
                emit(AuthState.INVALID)
            }

            is FirebaseAuthInvalidUserException -> {
                emit(AuthState.INVALID)
            }

            is FirebaseAuthWeakPasswordException -> {
                emit(AuthState.WEAK)
            }

            else -> {
                emit(AuthState.ERROR)
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun logout(): Flow<AuthState> = flow {
        auth.signOut()

        emit(AuthState.UNAUTHENTICATED)
    }.catch {
        emit(AuthState.ERROR)
    }.flowOn(Dispatchers.IO)

    override fun register(name: String?, email: String?, password: String?): Flow<AuthState> =
        flow {
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
                }

                is FirebaseAuthWeakPasswordException -> {
                    emit(AuthState.WEAK)
                }

                else -> {
                    emit(AuthState.ERROR)
                }
            }
        }.flowOn(Dispatchers.IO)

    override fun saveConsultationInformation(
        data: ConsultationFormModel
    ): Flow<ResourceState<out Nothing?>> = flow {
        emit(ResourceState.Loading(null))

        database.collection("consultations")
            .add(data)
            .await()

        emit(ResourceState.Success(null))
    }.catch { error ->
        emit(ResourceState.Error(error.message))
    }.flowOn(Dispatchers.IO)

    override fun saveDiagnosisInformation(
        data: DiagnosisFormModel
    ): Flow<ResourceState<out Nothing?>> = flow {
        emit(ResourceState.Loading(null))

        database.collection("diagnosis")
            .add(data)
            .await()

        emit(ResourceState.Success(null))
    }.catch { error ->
        emit(ResourceState.Error(error.message))
    }.flowOn(Dispatchers.IO)
}