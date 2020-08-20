package id.ac.esaunggul.breastcancerdetection.ui.main.user

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ac.esaunggul.breastcancerdetection.NavigationMainDirections
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.data.model.ConsultationModel
import id.ac.esaunggul.breastcancerdetection.data.model.DiagnosisModel
import id.ac.esaunggul.breastcancerdetection.data.model.UserModel
import id.ac.esaunggul.breastcancerdetection.data.repo.Repo
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.NavigationDispatcher
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.SharedPrefDispatcher
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.ToastDispatcher
import id.ac.esaunggul.breastcancerdetection.util.state.AuthState
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import id.ac.esaunggul.breastcancerdetection.util.validation.FormValidation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.DateFormat
import java.util.Date
import java.util.Locale

class UserViewModel
@ViewModelInject
constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val sharedPrefDispatcher: SharedPrefDispatcher,
    private val toastDispatcher: ToastDispatcher,
    private val repo: Repo
) : ViewModel() {

    private val _profile = MutableLiveData<UserModel>()
    val profile: LiveData<UserModel> = _profile

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

    private val _clickable = MutableLiveData<Boolean>(true)
    val clickable = _clickable

    private val _loading = MutableLiveData<Boolean>(false)
    val loading = _loading

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.fetchUserData().collect { state ->
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

        var state = commonValidation()

        if (concernField.value.isNullOrEmpty()) {
            concernError.value = R.string.form_empty
            state = false
        }

        if (state) {
            viewModelScope.launch(Dispatchers.IO) {
                val dateConverter =
                    DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
                val dateConverted: Date?
                dateConverted = dateField.value?.let { dateConverter.parse(it) } ?: Date()

                repo.saveConsultationInformation(
                    ConsultationModel(
                        profile.value?.uid,
                        nameField.value,
                        addressField.value,
                        historyField.value,
                        dateConverted,
                        concernField.value
                    )
                ).collect { state ->
                    when (state) {
                        is ResourceState.Success -> {
                            Timber.d("Successfully submitted the form, showing informational toast.")
                            withContext(Dispatchers.Main) {
                                release()
                                toastDispatcher.emit(R.string.form_submitted)
                            }
                            releaseClickableButtonBackground()
                        }

                        is ResourceState.Error -> {
                            Timber.e("An error occurred during transaction: ${state.code}")
                            withContext(Dispatchers.Main) {
                                toastDispatcher.emit(R.string.network_failed)
                            }
                            releaseClickableButtonBackground()
                        }

                        is ResourceState.Loading -> {
                            Timber.d("Submitting...")
                            setClickableButtonBackground()
                        }
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
                val dateConverter =
                    DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
                val dateConverted: Date?
                dateConverted = dateField.value?.let { dateConverter.parse(it) } ?: Date()

                repo.saveDiagnosisInformation(
                    DiagnosisModel(
                        profile.value?.uid,
                        nameField.value,
                        addressField.value,
                        historyField.value,
                        dateConverted
                    )
                ).collect { state ->
                    when (state) {
                        is ResourceState.Success -> {
                            Timber.d("Successfully submitted the form, showing informational toast.")
                            withContext(Dispatchers.Main) {
                                release()
                                toastDispatcher.emit(R.string.form_submitted)
                            }
                            releaseClickableButtonBackground()
                        }

                        is ResourceState.Error -> {
                            Timber.e("An error occurred during transaction: ${state.code}")
                            withContext(Dispatchers.Main) {
                                toastDispatcher.emit(R.string.network_failed)
                            }
                            releaseClickableButtonBackground()
                        }

                        is ResourceState.Loading -> {
                            Timber.d("Submitting...")
                            setClickableButtonBackground()
                        }
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.logout().collect { state ->
                when (state) {
                    AuthState.UNAUTHENTICATED -> {
                        withContext(Dispatchers.Main) {
                            release()
                            sharedPrefDispatcher.emit { sharedPref ->
                                with(sharedPref.edit()) {
                                    remove("id.ac.esaunggul.breastcancerdetection.ADMIN_KEY")
                                    commit()
                                }
                            }
                            navigationDispatcher.emit { navController ->
                                navController.navigate(NavigationMainDirections.actionUserLogout())
                            }
                        }
                    }

                    AuthState.ERROR -> {
                        Timber.e("Failed to logout")
                        withContext(Dispatchers.Main) {
                            toastDispatcher.emit(R.string.network_failed)
                        }
                    }

                    else -> Timber.e("Catastrophic error happened.")
                }
            }
        }
    }

    fun release() {
        releaseField()
        releaseError()
        releaseFocus()
        releaseClickableButton()
    }

    private fun commonValidation(): Boolean {
        var state = true
        if (nameField.value.isNullOrEmpty()) {
            nameError.value = R.string.form_empty
        } else if (FormValidation.isNameNotValid(nameField.value)) {
            nameError.value = R.string.name_invalid
            state = false
        }

        if (addressField.value.isNullOrEmpty()) {
            addressError.value = R.string.form_empty
            state = false
        }
        if (historyField.value.isNullOrEmpty()) {
            historyError.value = R.string.form_empty
            state = false
        }
        if (dateField.value.isNullOrEmpty()) {
            dateError.value = R.string.form_empty
            state = false
        }
        return state
    }

    private fun setClickableButtonBackground() {
        _clickable.postValue(false)
        _loading.postValue(true)
    }

    private fun releaseClickableButton() {
        _loading.value = false
        _clickable.value = true
    }

    private fun releaseClickableButtonBackground() {
        _loading.postValue(false)
        _clickable.postValue(true)
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