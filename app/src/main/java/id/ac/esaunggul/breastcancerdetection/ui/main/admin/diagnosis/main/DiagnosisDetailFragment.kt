package id.ac.esaunggul.breastcancerdetection.ui.main.admin.diagnosis.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentDiagnosisAdminDetailBinding
import id.ac.esaunggul.breastcancerdetection.ui.main.admin.diagnosis.DiagnosisViewModel

@AndroidEntryPoint
class DiagnosisDetailFragment : Fragment() {

    private val args: DiagnosisDetailFragmentArgs by navArgs()

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
        val diagnosisViewModel: DiagnosisViewModel by navGraphViewModels(R.id.fragment_navigation_diagnosis_admin) {
            defaultViewModelProviderFactory
        }

        val binding = FragmentDiagnosisAdminDetailBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.diagnosisViewModel = diagnosisViewModel
        binding.direction = DiagnosisDetailFragmentDirections
        binding.position = args.position

        return binding.root
    }
}