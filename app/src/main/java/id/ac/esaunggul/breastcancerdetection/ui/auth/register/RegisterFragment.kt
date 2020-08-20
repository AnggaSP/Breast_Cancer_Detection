package id.ac.esaunggul.breastcancerdetection.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.github.razir.progressbutton.bindProgressButton
import dagger.hilt.android.AndroidEntryPoint
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentRegisterBinding
import id.ac.esaunggul.breastcancerdetection.ui.auth.AuthViewModel
import id.ac.esaunggul.breastcancerdetection.util.extensions.applyInsets
import id.ac.esaunggul.breastcancerdetection.util.extensions.endSharedAxisTransition

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val authViewModel: AuthViewModel by navGraphViewModels(R.id.navigation_auth) {
        defaultViewModelProviderFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        endSharedAxisTransition()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.authViewModel = authViewModel
        viewLifecycleOwner.bindProgressButton(binding.registerButton)

        applyInsets(binding.registerParentLayout)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        authViewModel.release()
    }
}