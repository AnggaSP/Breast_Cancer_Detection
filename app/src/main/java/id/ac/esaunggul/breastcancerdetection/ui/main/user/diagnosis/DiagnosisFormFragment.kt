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

package id.ac.esaunggul.breastcancerdetection.ui.main.user.diagnosis

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.transition.platform.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentDiagnosisFormBinding
import id.ac.esaunggul.breastcancerdetection.ui.common.CommonDatePicker
import id.ac.esaunggul.breastcancerdetection.ui.main.user.UserViewModel
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import timber.log.Timber

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

        viewLifecycleOwner.bindProgressButton(binding.diagnosisConfirmButton)

        userViewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ResourceState.Success -> {
                    Timber.d("Successfully submitted the form, showing informational toast.")
                    Toast.makeText(requireContext(), R.string.form_submitted, Toast.LENGTH_LONG)
                        .show()
                    binding.diagnosisConfirmButton.hideProgress(R.string.diagnosis_form_button_hint)
                    userViewModel.release()
                    binding.diagnosisConfirmButton.isClickable = true
                }
                is ResourceState.Error -> {
                    Timber.e("An error occurred during transaction: ${state.code}")
                    Toast.makeText(requireContext(), R.string.network_failed, Toast.LENGTH_LONG)
                        .show()
                    binding.diagnosisConfirmButton.hideProgress(R.string.diagnosis_form_button_hint)
                    binding.diagnosisConfirmButton.isClickable = true
                }
                is ResourceState.Loading -> {
                    Timber.d("Submitting...")
                    binding.diagnosisConfirmButton.isClickable = false
                    binding.diagnosisConfirmButton.showProgress {
                        textMarginPx = 0
                        progressColor = Color.WHITE
                    }
                }
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        userViewModel.release()
    }

    fun showDatePicker() {
        val picker = CommonDatePicker().build(title = R.string.form_date_hint)
        picker.show(parentFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {
            userViewModel.dateField.value = picker.headerText
        }
    }
}