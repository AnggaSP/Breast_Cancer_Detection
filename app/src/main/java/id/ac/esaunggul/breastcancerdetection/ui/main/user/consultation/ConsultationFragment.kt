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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.github.razir.progressbutton.bindProgressButton
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentConsultationBinding
import id.ac.esaunggul.breastcancerdetection.ui.common.CommonDatePicker
import id.ac.esaunggul.breastcancerdetection.ui.main.user.UserViewModel

@AndroidEntryPoint
class ConsultationFragment : Fragment() {

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
        val binding = FragmentConsultationBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.handler = this
        binding.userViewModel = userViewModel
        viewLifecycleOwner.bindProgressButton(binding.consultationConfirmButton)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        userViewModel.release()
    }

    fun showDatePicker() {
        val picker = CommonDatePicker().build(title = R.string.mtrl_picker_text_input_date_hint)
        picker.show(parentFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {
            userViewModel.dateField.value = picker.headerText
        }
    }
}