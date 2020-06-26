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

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.esaunggul.breastcancerdetection.data.model.ArticleModel
import id.ac.esaunggul.breastcancerdetection.data.model.UserModel
import id.ac.esaunggul.breastcancerdetection.di.main.MainScope
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@MainScope
class MainRepo
@Inject
constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) {

    fun fetchArticles() = flow {
        emit(ResourceState.Loading<List<ArticleModel>>())

        val fetch = database.collection("articles").get().await()

        emit(ResourceState.Success(fetch.toObjects(ArticleModel::class.java)))
    }.catch { error ->
        emit(ResourceState.Error(error.message))
    }.flowOn(Dispatchers.IO)

    fun fetchUserData() = flow {
        emit(ResourceState.Loading<UserModel>())

        val user = auth.currentUser
        user?.let { data ->
            val userData = UserModel(data.uid, data.displayName, data.email, data.photoUrl)
            emit(ResourceState.Success(userData))
        }
    }.catch { error ->
        emit(ResourceState.Error(error.message))
    }.flowOn(Dispatchers.IO)

    fun saveConsultationInformation(
        name: String?,
        address: String?,
        history: String?,
        date: String?,
        concern: String?
    ) = flow {
        emit(ResourceState.Loading(null))

        val dateConverter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateConverted: Date?
        dateConverted = date?.let { dateConverter.parse(it) } ?: Date()

        val information = hashMapOf(
            "uid" to auth.currentUser?.uid,
            "name" to name,
            "address" to address,
            "history" to history,
            "date" to Timestamp(dateConverted),
            "concern" to concern
        )

        database.collection("consultations")
            .add(information)
            .await()

        emit(ResourceState.Success(null))
    }.catch { error ->
        emit(ResourceState.Error(error.message))
    }.flowOn(Dispatchers.IO)

    fun saveDiagnosisInformation(
        name: String?,
        address: String?,
        history: String?,
        date: String?
    ) = flow {
        emit(ResourceState.Loading(null))

        val dateConverter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateConverted: Date?
        dateConverted = date?.let { dateConverter.parse(it) } ?: Date()

        val information = hashMapOf(
            "uid" to auth.currentUser?.uid,
            "name" to name,
            "address" to address,
            "history" to history,
            "date" to Timestamp(dateConverted)
        )

        database.collection("diagnosis")
            .add(information)
            .await()

        emit(ResourceState.Success(null))
    }.catch { error ->
        emit(ResourceState.Error(error.message))
    }.flowOn(Dispatchers.IO)

    fun logout() = flow {
        emit(ResourceState.Loading(null))

        auth.signOut()

        emit(ResourceState.Success(null))
    }.catch { error ->
        emit(ResourceState.Error(error.message))
    }.flowOn(Dispatchers.IO)
}