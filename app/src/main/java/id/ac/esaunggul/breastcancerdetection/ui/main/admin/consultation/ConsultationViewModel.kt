package id.ac.esaunggul.breastcancerdetection.ui.main.admin.consultation

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.data.model.ConsultationModel
import id.ac.esaunggul.breastcancerdetection.data.repo.Repo
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.NavigationDispatcher
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.ToastDispatcher
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ConsultationViewModel
@ViewModelInject
constructor(
    private val navigationDispatcher: NavigationDispatcher,
    private val toastDispatcher: ToastDispatcher,
    private val repo: Repo
) : ViewModel() {

    private val _consultation = MutableLiveData<ResourceState<List<ConsultationModel>>>()
    val consultation: LiveData<ResourceState<List<ConsultationModel>>> = _consultation

    init {
        getConsultation()
    }

    fun acceptConsultation() {
        navigationDispatcher.emit { navController ->
            navController.navigateUp()
            toastDispatcher.emit(R.string.consultation_accepted)
        }
    }

    fun denyConsultation() {
        navigationDispatcher.emit { navController ->
            navController.navigateUp()
            toastDispatcher.emit(R.string.consultation_denied)
        }
    }

    private fun getConsultation() {
        viewModelScope.launch {
            repo.fetchConsultation().collect { data -> _consultation.value = data }
        }
    }
}