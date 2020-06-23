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

package id.ac.esaunggul.breastcancerdetection.ui.main.user.consultation

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
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.transition.platform.MaterialFadeThrough
import id.ac.esaunggul.breastcancerdetection.BreastCancerDetection
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentConsultationBinding
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

class ConsultationFragment : Fragment() {

    companion object {
        private const val TAG = "Consultation"
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
        val consultationViewModel: ConsultationViewModel by viewModels {
            mainViewModelFactory
        }

        val binding = FragmentConsultationBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        binding.consultationViewModel = consultationViewModel

        viewLifecycleOwner.bindProgressButton(binding.consultationConfirmButton)

        binding.consultationDateField.clicks()
            .onEach {
                val builder = MaterialDatePicker.Builder.datePicker()
                val constraint = CalendarConstraints.Builder()

                val date = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                date.add(Calendar.MONTH, 1)
                val maxDate = date.timeInMillis

                val dateValidatorMin = DateValidatorPointForward.now()
                val dateValidatorMax = DateValidatorPointBackward.before(maxDate)

                val listValidators = mutableListOf<DateValidator>()
                listValidators.add(dateValidatorMin)
                listValidators.add(dateValidatorMax)

                val validators = CompositeDateValidator.allOf(listValidators)
                constraint.setValidator(validators)

                builder.setTitleText(R.string.form_date_hint)
                builder.setCalendarConstraints(constraint.build())

                val picker = builder.build()

                picker.show(parentFragmentManager, picker.toString())
                picker.addOnPositiveButtonClickListener {
                    binding.consultationDateField.setText(picker.headerText)
                }
            }
            .launchIn(lifecycleScope)

        binding.consultationConfirmButton.clicks()
            .throttleFirst(1000)
            .onEach {
                binding.consultationNameLayout.error = null
                binding.consultationAddressLayout.error = null
                binding.consultationHistoryLayout.error = null
                binding.consultationDateLayout.error = null
                binding.consultationConcernsLayout.error = null
                when {
                    FormValidation.isNameNotValid(binding.consultationNameField.text.toString()) -> {
                        binding.consultationNameLayout.error = getString(R.string.name_invalid)
                        binding.consultationNameField.requestFocus()
                    }
                    binding.consultationAddressField.text.toString().isEmpty() -> {
                        binding.consultationAddressLayout.error = getString(R.string.form_empty)
                        binding.consultationAddressLayout.requestFocus()
                    }
                    binding.consultationHistoryField.text.toString().isEmpty() -> {
                        binding.consultationHistoryLayout.error = getString(R.string.form_empty)
                        binding.consultationHistoryField.requestFocus()
                    }
                    binding.consultationDateField.text.toString().isEmpty() -> {
                        binding.consultationDateLayout.error = getString(R.string.form_empty)
                        binding.consultationDateField.requestFocus()
                    }
                    binding.consultationConcernsField.text.toString().isEmpty() -> {
                        binding.consultationConcernsLayout.error = getString(R.string.form_empty)
                        binding.consultationConcernsField.requestFocus()
                    }
                    else -> consultationViewModel.saveConsultInfo()
                }
            }
            .launchIn(lifecycleScope)

        consultationViewModel.state.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ResourceState.Success -> {
                    Log.d(TAG, "Successfully submitted the form, showing informational toast.")
                    Toast.makeText(requireContext(), R.string.form_submitted, Toast.LENGTH_LONG)
                        .show()
                    binding.consultationConfirmButton.hideProgress(R.string.consultation_form_button_hint)
                    binding.consultationNameField.text = null
                    binding.consultationAddressField.text = null
                    binding.consultationHistoryField.text = null
                    binding.consultationDateField.text = null
                    binding.consultationConcernsField.text = null
                }
                is ResourceState.Error -> {
                    Log.e(TAG, "An error occurred during transaction: ${it.code}")
                    Toast.makeText(requireContext(), R.string.network_failed, Toast.LENGTH_LONG)
                        .show()
                    binding.consultationConfirmButton.hideProgress(R.string.consultation_form_button_hint)
                }
                is ResourceState.Loading -> {
                    Log.d(TAG, "Submitting...")
                    binding.consultationNameField.clearFocus()
                    binding.consultationAddressLayout.clearFocus()
                    binding.consultationHistoryField.clearFocus()
                    binding.consultationDateField.clearFocus()
                    binding.consultationConcernsField.clearFocus()
                    binding.consultationConfirmButton.showProgress {
                        textMarginPx = 0
                        progressColor = Color.WHITE
                    }
                }
            }
        })

        return binding.root
    }
}