package id.ac.esaunggul.breastcancerdetection.data.repo

import id.ac.esaunggul.breastcancerdetection.data.model.ArticleModel
import id.ac.esaunggul.breastcancerdetection.data.model.ConsultationFormModel
import id.ac.esaunggul.breastcancerdetection.data.model.DiagnosisFormModel
import id.ac.esaunggul.breastcancerdetection.data.model.UserModel
import id.ac.esaunggul.breastcancerdetection.util.state.AuthState
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import kotlinx.coroutines.flow.Flow

interface Repo {

    fun checkSession(): AuthState

    fun fetchArticles(): Flow<ResourceState<List<ArticleModel>>>

    fun fetchUserData(): Flow<ResourceState<UserModel>>

    fun login(email: String?, password: String?): Flow<AuthState>

    fun logout(): Flow<AuthState>

    fun register(name: String?, email: String?, password: String?): Flow<AuthState>

    fun saveConsultationInformation(data: ConsultationFormModel): Flow<ResourceState<out Nothing?>>

    fun saveDiagnosisInformation(data: DiagnosisFormModel): Flow<ResourceState<out Nothing?>>
}