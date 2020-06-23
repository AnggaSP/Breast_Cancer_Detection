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

package id.ac.esaunggul.breastcancerdetection.ui.main.user.profile

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
import com.google.android.material.transition.platform.MaterialFadeThrough
import id.ac.esaunggul.breastcancerdetection.BreastCancerDetection
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentProfileBinding
import id.ac.esaunggul.breastcancerdetection.util.extensions.throttleFirst
import id.ac.esaunggul.breastcancerdetection.util.factory.MainViewModelFactory
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks
import javax.inject.Inject

class ProfileFragment : Fragment() {

    companion object {
        private const val TAG = "Profile"
    }

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory

    override fun onAttach(context: Context) {
        (requireActivity().application as BreastCancerDetection).mainComponent().inject(this)

        super.onAttach(context)
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
        val profileViewModel: ProfileViewModel by viewModels {
            mainViewModelFactory
        }

        val binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        binding.profileViewModel = profileViewModel

        viewLifecycleOwner.bindProgressButton(binding.profileLogOut)

        binding.profileLogOut.clicks()
            .throttleFirst(1000)
            .onEach {
                profileViewModel.logout()
            }
            .launchIn(lifecycleScope)

        profileViewModel.state.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ResourceState.Success -> {
                    (requireActivity().application as BreastCancerDetection).releaseMainComponent()
                    findNavController().navigate(ProfileFragmentDirections.actionLogout())
                }
                is ResourceState.Error -> {
                    Log.e(TAG, "Failed to logout")
                    Log.e(TAG, "Reason: ${it.code}")
                    Toast.makeText(requireContext(), R.string.network_failed, Toast.LENGTH_LONG)
                        .show()
                    binding.profileLogOut.hideProgress(R.string.button_log_out)
                }
                is ResourceState.Loading -> {
                    Log.d(TAG, "Loading...")
                    binding.profileLogOut.showProgress {
                        textMarginPx = 0
                        progressColor = Color.WHITE
                    }
                }
            }
        })

        return binding.root
    }
}