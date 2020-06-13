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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import id.ac.esaunggul.breastcancerdetection.BreastCancerDetection
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentRegisterBinding
import id.ac.esaunggul.breastcancerdetection.ui.auth.AuthViewModel
import id.ac.esaunggul.breastcancerdetection.ui.auth.AuthViewModelFactory
import id.ac.esaunggul.breastcancerdetection.ui.common.BaseFragment
import id.ac.esaunggul.breastcancerdetection.util.extensions.throttleFirst
import id.ac.esaunggul.breastcancerdetection.util.state.AuthState
import id.ac.esaunggul.breastcancerdetection.util.validation.FormValidation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import reactivecircus.flowbinding.android.view.clicks
import javax.inject.Inject

class RegisterFragment : BaseFragment() {

    companion object {
        private const val TAG = "Register"
    }

    @Inject
    lateinit var authViewModelFactory: AuthViewModelFactory

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
        val authViewModel: AuthViewModel by viewModels {
            authViewModelFactory
        }

        val binding: FragmentRegisterBinding by binds(
            R.layout.fragment_register,
            container
        )

        binding.lifecycleOwner = this

        applyInsets(binding.registerParentLayout)

        viewLifecycleOwner.bindProgressButton(binding.registerButton)

        binding.registerButton.clicks()
            .throttleFirst(1000)
            .onEach {
                binding.registerNameLayout.error = null
                binding.registerEmailLayout.error = null
                binding.registerPasswordLayout.error = null
                when {
                    FormValidation.isNameNotValid(binding.registerNameField.text.toString()) -> {
                        binding.registerNameLayout.error = getString(R.string.name_invalid)
                        binding.registerNameField.requestFocus()
                    }
                    FormValidation.isEmailNotValid(binding.registerEmailField.text.toString()) -> {
                        binding.registerEmailLayout.error = getString(R.string.email_invalid)
                        binding.registerEmailField.requestFocus()
                    }
                    FormValidation.isPasswordWeak(binding.registerPasswordField.text.toString()) -> {
                        binding.registerPasswordLayout.error = getString(R.string.password_weak)
                        binding.registerPasswordField.requestFocus()
                    }
                    else -> {
                        lifecycleScope.launch(Dispatchers.IO) {
                            authViewModel.register(
                                binding.registerNameField.text.toString(),
                                binding.registerEmailField.text.toString(),
                                binding.registerPasswordField.text.toString()
                            )
                        }
                    }
                }
            }
            .launchIn(lifecycleScope)

        authViewModel.authState.observe(viewLifecycleOwner, Observer {
            when (it) {
                AuthState.AUTHENTICATED -> {
                    (requireActivity().application as BreastCancerDetection).releaseAuthComponent()
                    view?.findNavController()
                        ?.navigate(RegisterFragmentDirections.actionRegisterAuthenticated())
                }
                AuthState.UNAUTHENTICATED -> {
                    binding.registerButton.hideProgress(R.string.button_register)
                }
                AuthState.LOADING -> {
                    Log.d(TAG, "Loading the data...")
                    binding.registerButton.showProgress {
                        textMarginPx = 0
                        progressColor = Color.WHITE
                    }
                }
                AuthState.COLLIDE -> {
                    binding.registerEmailLayout.error = getString(R.string.email_exist)
                    binding.registerEmailField.requestFocus()
                }
                AuthState.WEAK -> {
                    Log.d(TAG, "Weak password is being passed.")
                    Log.d(TAG, "Please check if the validation is doing its job.")
                    binding.registerPasswordLayout.error = getString(R.string.password_weak)
                    binding.registerPasswordField.requestFocus()
                }
                AuthState.ERROR -> {
                    Log.e(TAG, "A network error has occurred.")
                    Toast.makeText(requireActivity(), R.string.auth_failed, Toast.LENGTH_LONG)
                        .show()
                }
                else -> {
                    Log.e(TAG, "Catastrophic error happened.")
                }
            }
        })

        return binding.root
    }
}