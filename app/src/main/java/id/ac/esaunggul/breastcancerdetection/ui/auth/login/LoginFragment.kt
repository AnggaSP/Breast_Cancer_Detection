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

package id.ac.esaunggul.breastcancerdetection.ui.auth.login

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import id.ac.esaunggul.breastcancerdetection.BreastCancerDetection
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentLoginBinding
import id.ac.esaunggul.breastcancerdetection.ui.auth.AuthViewModel
import id.ac.esaunggul.breastcancerdetection.util.extensions.applyInsets
import id.ac.esaunggul.breastcancerdetection.util.extensions.endSharedAxisTransition
import id.ac.esaunggul.breastcancerdetection.util.extensions.throttleFirst
import id.ac.esaunggul.breastcancerdetection.util.factory.AuthViewModelFactory
import id.ac.esaunggul.breastcancerdetection.util.state.AuthState
import id.ac.esaunggul.breastcancerdetection.util.validation.FormValidation
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks
import javax.inject.Inject

class LoginFragment : Fragment() {

    companion object {
        private const val TAG = "Login"
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

        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        applyInsets(binding.loginParentLayout)

        viewLifecycleOwner.bindProgressButton(binding.loginButton)

        binding.loginButton.clicks()
            .throttleFirst(1000)
            .onEach {
                binding.loginEmailLayout.error = null
                binding.loginPasswordLayout.error = null
                when {
                    FormValidation.isEmailNotValid(binding.loginEmailField.text.toString()) -> {
                        binding.loginEmailLayout.error = getString(R.string.email_invalid)
                        binding.loginEmailField.requestFocus()
                    }
                    FormValidation.isPasswordWeak(binding.loginPasswordField.text.toString()) -> {
                        binding.loginPasswordLayout.error = getString(R.string.password_weak)
                        binding.loginPasswordField.requestFocus()
                    }
                    else -> {
                        authViewModel.login(
                            binding.loginEmailField.text.toString(),
                            binding.loginPasswordField.text.toString()
                        )
                    }
                }
            }
            .launchIn(lifecycleScope)

        authViewModel.authState.observe(viewLifecycleOwner, Observer {
            when (it) {
                AuthState.AUTHENTICATED -> {
                    (requireActivity().application as BreastCancerDetection).releaseAuthComponent()
                    findNavController().navigate(LoginFragmentDirections.actionLoginAuthenticated())
                }
                AuthState.UNAUTHENTICATED -> {
                    binding.loginButton.hideProgress(R.string.button_login)
                }
                AuthState.LOADING -> {
                    Log.d(TAG, "Loading the data...")
                    binding.loginEmailField.clearFocus()
                    binding.loginPasswordField.clearFocus()
                    binding.loginButton.showProgress {
                        textMarginPx = 0
                        progressColor = Color.WHITE
                    }
                }
                AuthState.INVALID -> {
                    Log.d(TAG, "Authentication failed.")
                    binding.loginEmailLayout.error = getString(R.string.login_email_invalid)
                    binding.loginPasswordLayout.error = getString(R.string.login_password_invalid)
                    binding.loginEmailField.requestFocus()
                }
                AuthState.ERROR -> {
                    Log.e(TAG, "A network error has occurred.")
                    Toast.makeText(requireActivity(), R.string.network_failed, Toast.LENGTH_LONG)
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