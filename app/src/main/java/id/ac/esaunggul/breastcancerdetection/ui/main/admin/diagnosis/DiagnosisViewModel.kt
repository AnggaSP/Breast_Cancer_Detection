package id.ac.esaunggul.breastcancerdetection.ui.main.admin.diagnosis

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ac.esaunggul.breastcancerdetection.data.api.DetectionApi
import id.ac.esaunggul.breastcancerdetection.data.model.ConsultationModel
import id.ac.esaunggul.breastcancerdetection.data.model.DiagnosisModel
import id.ac.esaunggul.breastcancerdetection.data.repo.Repo
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DiagnosisViewModel
@ViewModelInject
constructor(
    private val repo: Repo
) : ViewModel() {

    private val _diagnosis = MutableLiveData<ResourceState<List<DiagnosisModel>>>()
    val diagnosis: LiveData<ResourceState<List<DiagnosisModel>>> = _diagnosis

    init {
        getDiagnosis()
    }

    private fun getDiagnosis() {
        viewModelScope.launch {
            repo.fetchDiagnosis().collect { data -> _diagnosis.value = data }
        }
    }
}