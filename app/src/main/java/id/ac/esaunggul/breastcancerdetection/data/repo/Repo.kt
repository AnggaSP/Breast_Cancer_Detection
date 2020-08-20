package id.ac.esaunggul.breastcancerdetection.data.repo

import id.ac.esaunggul.breastcancerdetection.data.model.ArticleModel
import id.ac.esaunggul.breastcancerdetection.data.model.ConsultationModel
import id.ac.esaunggul.breastcancerdetection.data.model.DiagnosisModel
import id.ac.esaunggul.breastcancerdetection.data.model.UserModel
import id.ac.esaunggul.breastcancerdetection.util.state.AuthState
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import id.ac.esaunggul.breastcancerdetection.util.state.UserPrivilege
import kotlinx.coroutines.flow.Flow

interface Repo {

    fun checkSession(): AuthState

    fun fetchArticles(): Flow<ResourceState<List<ArticleModel>>>

    fun fetchConsultation(): Flow<ResourceState<List<ConsultationModel>>>

    fun fetchDiagnosis(): Flow<ResourceState<List<DiagnosisModel>>>

    fun fetchUserData(): Flow<ResourceState<UserModel>>

    fun fetchUserPrivilege(): Flow<UserPrivilege>

    fun login(email: String?, password: String?): Flow<AuthState>

    fun logout(): Flow<AuthState>

    fun register(name: String?, email: String?, password: String?): Flow<AuthState>

    fun saveConsultationInformation(data: ConsultationModel): Flow<ResourceState<out Nothing?>>

    fun saveDiagnosisInformation(data: DiagnosisModel): Flow<ResourceState<out Nothing?>>
}