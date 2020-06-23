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
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.transition.platform.MaterialFadeThrough
import id.ac.esaunggul.breastcancerdetection.BreastCancerDetection
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentDiagnosisFormBinding
import id.ac.esaunggul.breastcancerdetection.util.extensions.throttleFirst
import id.ac.esaunggul.breastcancerdetection.util.factory.MainViewModelFactory
import id.ac.esaunggul.breastcancerdetection.util.state.ResourceState
import id.ac.esaunggul.breastcancerdetection.util.validation.FormValidation
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

class DiagnosisFormFragment : Fragment() {

    companion object {
        private const val TAG = "Diagnosis"
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
        val diagnosisViewModel: DiagnosisViewModel by viewModels {
            mainViewModelFactory
        }

        val binding = FragmentDiagnosisFormBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        binding.diagnosisViewModel = diagnosisViewModel

        viewLifecycleOwner.bindProgressButton(binding.diagnosisConfirmButton)

        binding.diagnosisDateField.clicks()
            .onEach {
                val builder = MaterialDatePicker.Builder.datePicker()
                val constraint = CalendarConstraints.Builder()

                val date = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                date.add(Calendar.MONTH, 1)
                val maxDate = date.timeInMillis

                val dateValidatorMin = DateValidatorPointForward.now()
                val dateValidatorMax = DateValidatorPointBackward.before(maxDate)

                val listValidators = mutableListOf<CalendarConstraints.DateValidator>()
                listValidators.add(dateValidatorMin)
                listValidators.add(dateValidatorMax)

                val validators = CompositeDateValidator.allOf(listValidators)
                constraint.setValidator(validators)

                builder.setTitleText(R.string.form_date_hint)
                builder.setCalendarConstraints(constraint.build())

                val picker = builder.build()

                picker.show(parentFragmentManager, picker.toString())
                picker.addOnPositiveButtonClickListener {
                    binding.diagnosisDateField.setText(picker.headerText)
                }
            }
            .launchIn(lifecycleScope)

        binding.diagnosisConfirmButton.clicks()
            .throttleFirst(1000)
            .onEach {
                binding.diagnosisNameLayout.error = null
                binding.diagnosisAddressLayout.error = null
                binding.diagnosisHistoryLayout.error = null
                binding.diagnosisDateLayout.error = null
                when {
                    FormValidation.isNameNotValid(binding.diagnosisNameField.text.toString()) -> {
                        binding.diagnosisNameLayout.error = getString(R.string.name_invalid)
                        binding.diagnosisNameField.requestFocus()
                    }
                    binding.diagnosisAddressField.text.toString().isEmpty() -> {
                        binding.diagnosisAddressLayout.error = getString(R.string.form_empty)
                        binding.diagnosisAddressLayout.requestFocus()
                    }
                    binding.diagnosisHistoryField.text.toString().isEmpty() -> {
                        binding.diagnosisHistoryLayout.error = getString(R.string.form_empty)
                        binding.diagnosisHistoryField.requestFocus()
                    }
                    binding.diagnosisDateField.text.toString().isEmpty() -> {
                        binding.diagnosisDateLayout.error = getString(R.string.form_empty)
                        binding.diagnosisDateField.requestFocus()
                    }
                    else -> diagnosisViewModel.saveDiagnosisInfo()
                }
            }
            .launchIn(lifecycleScope)

        diagnosisViewModel.state.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ResourceState.Success -> {
                    Log.d(TAG, "Successfully submitted the form, showing informational toast.")
                    Toast.makeText(requireContext(), R.string.form_submitted, Toast.LENGTH_LONG)
                        .show()
                    binding.diagnosisConfirmButton.hideProgress(R.string.diagnosis_form_button_hint)
                    binding.diagnosisNameField.text = null
                    binding.diagnosisAddressField.text = null
                    binding.diagnosisHistoryField.text = null
                    binding.diagnosisDateField.text = null
                }
                is ResourceState.Error -> {
                    Log.e(TAG, "An error occurred during transaction: ${it.code}")
                    Toast.makeText(requireContext(), R.string.network_failed, Toast.LENGTH_LONG)
                        .show()
                    binding.diagnosisConfirmButton.hideProgress(R.string.diagnosis_form_button_hint)
                }
                is ResourceState.Loading -> {
                    Log.d(TAG, "Submitting...")
                    binding.diagnosisNameField.clearFocus()
                    binding.diagnosisAddressLayout.clearFocus()
                    binding.diagnosisHistoryField.clearFocus()
                    binding.diagnosisDateField.clearFocus()
                    binding.diagnosisConfirmButton.showProgress {
                        textMarginPx = 0
                        progressColor = Color.WHITE
                    }
                }
            }
        })

        return binding.root
    }
}