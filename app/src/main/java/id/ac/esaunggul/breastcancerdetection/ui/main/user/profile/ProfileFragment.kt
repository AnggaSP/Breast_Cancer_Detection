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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.transition.platform.MaterialFadeThrough
import id.ac.esaunggul.breastcancerdetection.BreastCancerDetection
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentProfileBinding
import id.ac.esaunggul.breastcancerdetection.ui.main.user.UserViewModel
import id.ac.esaunggul.breastcancerdetection.util.factory.MainViewModelFactory
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import timber.log.Timber
import javax.inject.Inject

class ProfileFragment : Fragment() {

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
        val userViewModel: UserViewModel by navGraphViewModels(R.id.navigation_main) {
            mainViewModelFactory
        }

        val binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.userViewModel = userViewModel

        userViewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ResourceState.Success -> {
                    (requireActivity().application as BreastCancerDetection).releaseMainComponent()
                    userViewModel.release()
                    findNavController().navigate(ProfileFragmentDirections.actionLogout())
                }
                is ResourceState.Error -> {
                    Timber.e("Failed to logout")
                    Timber.e("Reason: ${state.code}")
                    Toast.makeText(requireContext(), R.string.network_failed, Toast.LENGTH_LONG)
                        .show()
                }
            }
        })

        return binding.root
    }
}