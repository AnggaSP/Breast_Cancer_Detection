package id.ac.esaunggul.breastcancerdetection.ui.main.admin.profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ac.esaunggul.breastcancerdetection.NavigationAdminDirections
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.data.model.UserModel
import id.ac.esaunggul.breastcancerdetection.data.repo.Repo
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.NavigationDispatcher
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.SharedPrefDispatcher
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.ToastDispatcher
import id.ac.esaunggul.breastcancerdetection.util.state.AuthState
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ProfileViewModel
@ViewModelInject
constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val sharedPrefDispatcher: SharedPrefDispatcher,
    private val toastDispatcher: ToastDispatcher,
    private val repo: Repo
) : ViewModel() {

    private val _profile = MutableLiveData<UserModel>()
    val profile: LiveData<UserModel> = _profile

    init {
        fetchUser()
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.logout().collect { state ->
                when (state) {
                    AuthState.UNAUTHENTICATED -> {
                        withContext(Dispatchers.Main) {
                            sharedPrefDispatcher.emit { sharedPref ->
                                with(sharedPref.edit()) {
                                    remove("id.ac.esaunggul.breastcancerdetection.ADMIN_KEY")
                                    commit()
                                }
                            }
                            navigationDispatcher.emit { navController ->
                                navController.navigate(NavigationAdminDirections.actionAdminLogout())
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

    private fun fetchUser() {
        viewModelScope.launch {
            repo.fetchUserData().collect { state ->
                when (state) {
                    is ResourceState.Success -> state.data?.let { data -> _profile.value = data }

                    is ResourceState.Error -> {
                        Timber.e("Cannot fetch userdata, using default name.")
                        Timber.e("Reason: ${state.code}")
                        _profile.value = UserModel("ERROR", "User", "user@error.com")
                    }

                    is ResourceState.Loading -> Timber.d("Fetching the userdata")
                }
            }
        }
    }
}