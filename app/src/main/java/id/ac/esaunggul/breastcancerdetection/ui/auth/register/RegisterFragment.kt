/*
 * Copyright 2020 Angga Satya Putra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package id.ac.esaunggul.breastcancerdetection.ui.auth.register

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import id.ac.esaunggul.breastcancerdetection.BreastCancerDetection
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentRegisterBinding
import id.ac.esaunggul.breastcancerdetection.ui.auth.AuthViewModel
import id.ac.esaunggul.breastcancerdetection.util.extensions.applyInsets
import id.ac.esaunggul.breastcancerdetection.util.extensions.endSharedAxisTransition
import id.ac.esaunggul.breastcancerdetection.util.factory.AuthViewModelFactory
import id.ac.esaunggul.breastcancerdetection.util.state.AuthState
import timber.log.Timber
import javax.inject.Inject

class RegisterFragment : Fragment() {

    @Inject
    lateinit var authViewModelFactory: AuthViewModelFactory

    private val authViewModel: AuthViewModel by navGraphViewModels(R.id.navigation_auth) {
        authViewModelFactory
    }

    override fun onAttach(context: Context) {
        (requireActivity().application as BreastCancerDetection).authComponent().inject(this)

        super.onAttach(context)
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

        applyInsets(binding.registerParentLayout)

        viewLifecycleOwner.bindProgressButton(binding.registerButton)

        authViewModel.authState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                AuthState.AUTHENTICATED -> {
                    (requireActivity().application as BreastCancerDetection).releaseAuthComponent()
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterAuthenticated())
                }
                AuthState.UNAUTHENTICATED -> {
                    binding.registerButton.hideProgress(R.string.button_register)
                    binding.registerButton.isClickable = true
                }
                AuthState.LOADING -> {
                    Timber.d("Loading the data...")
                    binding.registerButton.isClickable = false
                    binding.registerButton.showProgress {
                        textMarginPx = 0
                        progressColor = Color.WHITE
                    }
                }
                AuthState.COLLIDE -> {
                    authViewModel.emailError.value = R.string.email_exist
                }
                AuthState.WEAK -> {
                    Timber.d("Weak password is being passed.")
                    Timber.d("Please check if the validation is doing its job.")
                    authViewModel.passwordError.value = R.string.password_weak
                }
                AuthState.ERROR -> {
                    Timber.e("An error has occurred.")
                    Toast.makeText(requireActivity(), R.string.network_failed, Toast.LENGTH_LONG)
                        .show()
                }
                else -> {
                    Timber.e("Catastrophic error happened.")
                }
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        authViewModel.release()
    }
}