package id.ac.esaunggul.breastcancerdetection.ui.main.admin.diagnosis.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentDiagnosisAdminResultBinding
import id.ac.esaunggul.breastcancerdetection.ui.main.admin.diagnosis.DiagnosisViewModel
import timber.log.Timber

@AndroidEntryPoint
class DiagnosisResultFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val diagnosisViewModel: DiagnosisViewModel by navGraphViewModels(
            R.id.fragment_navigation_diagnosis_admin
        ) {
            defaultViewModelProviderFactory
        }

        val diagnosisUploadViewModel: DiagnosisUploadViewModel by navGraphViewModels(
            R.id.fragment_navigation_diagnosis_upload_admin
        ) {
            defaultViewModelProviderFactory
        }

        val binding = FragmentDiagnosisAdminResultBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.diagnosisViewModel = diagnosisViewModel
        binding.diagnosisUploadViewModel = diagnosisUploadViewModel

        return binding.root
    }
}