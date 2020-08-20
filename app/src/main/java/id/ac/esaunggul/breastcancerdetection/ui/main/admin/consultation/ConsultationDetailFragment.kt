package id.ac.esaunggul.breastcancerdetection.ui.main.admin.consultation

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
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentConsultationAdminDetailBinding

@AndroidEntryPoint
class ConsultationDetailFragment : Fragment() {

    private val args: ConsultationDetailFragmentArgs by navArgs()

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
        val consultationViewModel: ConsultationViewModel by navGraphViewModels(R.id.fragment_navigation_consultation_admin) {
            defaultViewModelProviderFactory
        }

        val binding = FragmentConsultationAdminDetailBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.consultationViewModel = consultationViewModel
        binding.position = args.position

        return binding.root
    }
}