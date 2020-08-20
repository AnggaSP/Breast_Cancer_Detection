package id.ac.esaunggul.breastcancerdetection.ui.main.user.diagnosis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.github.razir.progressbutton.bindProgressButton
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentDiagnosisFormBinding
import id.ac.esaunggul.breastcancerdetection.ui.common.CommonDatePicker
import id.ac.esaunggul.breastcancerdetection.ui.main.user.UserViewModel

@AndroidEntryPoint
class DiagnosisFormFragment : Fragment() {

    private val userViewModel: UserViewModel by navGraphViewModels(R.id.navigation_main) {
        defaultViewModelProviderFactory
    }

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
        val binding = FragmentDiagnosisFormBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.handler = this
        binding.userViewModel = userViewModel
        viewLifecycleOwner.bindProgressButton(binding.diagnosisFormConfirmButton)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        userViewModel.release()
    }

    fun showDatePicker() {
        val picker = CommonDatePicker()
            .build(title = R.string.mtrl_picker_text_input_date_hint)
        picker.show(parentFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {
            userViewModel.dateField.value = picker.headerText
        }
    }
}