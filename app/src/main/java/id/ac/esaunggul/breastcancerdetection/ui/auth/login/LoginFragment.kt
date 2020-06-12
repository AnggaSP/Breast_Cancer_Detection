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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import id.ac.esaunggul.breastcancerdetection.BreastCancerDetection
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentLoginBinding
import id.ac.esaunggul.breastcancerdetection.ui.auth.AuthViewModel
import id.ac.esaunggul.breastcancerdetection.ui.auth.AuthViewModelFactory
import id.ac.esaunggul.breastcancerdetection.ui.common.BaseFragment
import id.ac.esaunggul.breastcancerdetection.util.AuthState
import id.ac.esaunggul.breastcancerdetection.util.FormValidation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginFragment : BaseFragment() {

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

        val binding: FragmentLoginBinding by binds(
            R.layout.fragment_login,
            container
        )

        binding.lifecycleOwner = this

        applyInsets(binding.loginParentLayout)

        viewLifecycleOwner.bindProgressButton(binding.loginButton)

        binding.loginButton.setOnClickListener {
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
                    lifecycleScope.launch(Dispatchers.IO) {
                        authViewModel.login(
                            binding.loginEmailField.text.toString(),
                            binding.loginPasswordField.text.toString()
                        )
                    }
                }
            }
        }

        authViewModel.authState.observe(viewLifecycleOwner, Observer {
            when (it) {
                AuthState.AUTHENTICATED -> {
                    (requireActivity().application as BreastCancerDetection).releaseAuthComponent()
                    view?.findNavController()
                        ?.navigate(LoginFragmentDirections.actionLoginAuthenticated())
                }
                AuthState.UNAUTHENTICATED -> {
                    binding.loginButton.hideProgress(R.string.button_login)
                }
                AuthState.LOADING -> {
                    Log.d(TAG, "Loading the data...")
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