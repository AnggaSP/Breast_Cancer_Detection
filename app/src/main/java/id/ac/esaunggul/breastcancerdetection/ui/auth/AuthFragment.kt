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

package id.ac.esaunggul.breastcancerdetection.ui.auth

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import id.ac.esaunggul.breastcancerdetection.BreastCancerDetection
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentAuthBinding
import id.ac.esaunggul.breastcancerdetection.ui.common.BaseFragment
import id.ac.esaunggul.breastcancerdetection.util.AuthState
import javax.inject.Inject

class AuthFragment : BaseFragment() {

    companion object {
        private const val TAG = "Auth"
    }

    @Inject
    lateinit var authViewModelFactory: AuthViewModelFactory

    override fun onAttach(context: Context) {
        (requireActivity().application as BreastCancerDetection).authComponent().inject(this)

        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startSharedAxisTransition()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val authViewModel: AuthViewModel by viewModels {
            authViewModelFactory
        }

        val binding: FragmentAuthBinding by binds(
            R.layout.fragment_auth,
            container
        )

        binding.lifecycleOwner = this

        applyInsets(binding.authParentLayout)

        binding.authLoginButton.setOnClickListener { view ->
            view.findNavController().navigate(AuthFragmentDirections.actionAuthToLogin())
        }
        binding.authRegisterButton.setOnClickListener { view ->
            view.findNavController().navigate(AuthFragmentDirections.actionAuthToRegister())
        }

        authViewModel.authState.observe(viewLifecycleOwner, Observer {
            when (it) {
                AuthState.AUTHENTICATED -> {
                    Log.d(TAG, "User has logged in, continuing the session.")
                    view?.findNavController()
                        ?.navigate(AuthFragmentDirections.actionUserAuthenticated())
                }
                AuthState.UNAUTHENTICATED -> {
                    Log.d(
                        TAG,
                        "No user session has been found, showing the auth screen."
                    )
                }
                else -> Log.e(TAG, "Catastrophic error happened.")
            }
        })

        return binding.root
    }
}