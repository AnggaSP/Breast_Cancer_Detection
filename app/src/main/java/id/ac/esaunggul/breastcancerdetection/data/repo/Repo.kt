package id.ac.esaunggul.breastcancerdetection.data.repo

import id.ac.esaunggul.breastcancerdetection.data.model.ArticleModel
import id.ac.esaunggul.breastcancerdetection.data.model.UserModel
import id.ac.esaunggul.breastcancerdetection.util.state.AuthState
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import kotlinx.coroutines.flow.Flow

interface Repo {

    fun checkSession(): AuthState

    fun fetchArticles(): Flow<ResourceState<List<ArticleModel>>>

    fun fetchUserData(): Flow<ResourceState<UserModel>>

    fun login(email: String?, password: String?): Flow<AuthState>

    fun logout(): Flow<ResourceState<out Nothing?>>

    fun register(name: String?, email: String?, password: String?): Flow<AuthState>

    fun saveConsultationInformation(
        name: String?,
        address: String?,
        history: String?,
        date: String?,
        concern: String?
    ): Flow<ResourceState<out Nothing?>>

    fun saveDiagnosisInformation(
        name: String?,
        address: String?,
        history: String?,
        date: String?
    ): Flow<ResourceState<out Nothing?>>
}