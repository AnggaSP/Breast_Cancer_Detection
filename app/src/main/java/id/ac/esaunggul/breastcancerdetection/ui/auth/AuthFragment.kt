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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.transition.platform.MaterialFadeThrough
import id.ac.esaunggul.breastcancerdetection.BreastCancerDetection
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentAuthBinding
import id.ac.esaunggul.breastcancerdetection.util.extensions.applyInsets
import id.ac.esaunggul.breastcancerdetection.util.extensions.startSharedAxisTransition
import id.ac.esaunggul.breastcancerdetection.util.factory.AuthViewModelFactory
import id.ac.esaunggul.breastcancerdetection.util.state.AuthState
import timber.log.Timber
import javax.inject.Inject

class AuthFragment : Fragment() {

    @Inject
    lateinit var authViewModelFactory: AuthViewModelFactory

    override fun onAttach(context: Context) {
        (requireActivity().application as BreastCancerDetection).authComponent().inject(this)

        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startSharedAxisTransition()

        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val authViewModel: AuthViewModel by navGraphViewModels(R.id.navigation_auth) {
            authViewModelFactory
        }

        val binding = FragmentAuthBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.direction = AuthFragmentDirections

        applyInsets(binding.authParentLayout)

        authViewModel.authState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                AuthState.AUTHENTICATED -> {
                    Timber.d("User has logged in, continuing the session.")
                    (requireActivity().application as BreastCancerDetection).releaseAuthComponent()
                    findNavController().navigate(AuthFragmentDirections.actionUserAuthenticated())
                }
                AuthState.UNAUTHENTICATED -> {
                    Timber.d("No user session has been found, showing the auth screen.")
                }
                else -> Timber.e("Catastrophic error happened.")
            }
        })

        return binding.root
    }
}