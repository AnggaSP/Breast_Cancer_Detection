package id.ac.esaunggul.breastcancerdetection.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import id.ac.esaunggul.breastcancerdetection.data.model.ArticleModel
import id.ac.esaunggul.breastcancerdetection.data.model.ConsultationModel
import id.ac.esaunggul.breastcancerdetection.data.model.DiagnosisModel
import id.ac.esaunggul.breastcancerdetection.data.model.UserModel
import id.ac.esaunggul.breastcancerdetection.util.state.AuthState
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import id.ac.esaunggul.breastcancerdetection.util.state.UserPrivilege
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

        val fetch = database.collection("articles")
            .orderBy("date_published", Query.Direction.DESCENDING)
            .get()
            .await()

        emit(ResourceState.Success(fetch.toObjects(ArticleModel::class.java)))
    }.catch { error ->
        emit(ResourceState.Error(error.message))
    }.flowOn(Dispatchers.IO)

    override fun fetchConsultation(): Flow<ResourceState<List<ConsultationModel>>> = flow {
        emit(ResourceState.Loading<List<ConsultationModel>>())

        val fetch = database.collection("consultations")
            .orderBy("date")
            .get()
            .await()

        emit(ResourceState.Success(fetch.toObjects(ConsultationModel::class.java)))
    }.catch { error ->
        emit(ResourceState.Error(error.message))
    }.flowOn(Dispatchers.IO)

    override fun fetchDiagnosis(): Flow<ResourceState<List<DiagnosisModel>>> = flow {
        emit(ResourceState.Loading<List<DiagnosisModel>>())

        val fetch = database.collection("diagnosis")
            .orderBy("date")
            .get()
            .await()

        emit(ResourceState.Success(fetch.toObjects(DiagnosisModel::class.java)))
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

    override fun fetchUserPrivilege(): Flow<UserPrivilege> = flow {
        val user = auth.currentUser
        user?.let { data ->
            val result = data.getIdToken(false).await()
            val isAdmin = result.claims["isAdmin"] as? Boolean

            isAdmin?.let { value ->
                if (value) {
                    emit(UserPrivilege.ADMIN)
                } else {
                    emit(UserPrivilege.NORMAL)
                }
            } ?: run {
                emit(UserPrivilege.ERROR)
            }
        }
    }.catch {
        emit(UserPrivilege.ERROR)
    }.flowOn(Dispatchers.IO)

    override fun login(email: String?, password: String?): Flow<AuthState> = flow {
        emit(AuthState.LOADING)

        if (email != null && password != null) {
            auth.signInWithEmailAndPassword(email, password).await()
            emit(AuthState.AUTHENTICATED)
        } else {
            emit(AuthState.INVALID)
        }
    }.catch { error ->
        when (error) {
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
        }.catch { error ->
            when (error) {
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
        data: ConsultationModel
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
        data: DiagnosisModel
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