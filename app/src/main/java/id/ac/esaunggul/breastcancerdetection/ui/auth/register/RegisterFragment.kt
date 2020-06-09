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

package id.ac.esaunggul.breastcancerdetection.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentRegisterBinding
import id.ac.esaunggul.breastcancerdetection.ui.auth.login.LoginFragmentDirections
import id.ac.esaunggul.breastcancerdetection.ui.common.BaseFragment

class RegisterFragment : BaseFragment() {

    companion object {
        private const val TAG = "Register"
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
        val binding: FragmentRegisterBinding by binds(
            R.layout.fragment_register,
            container
        )

        binding.lifecycleOwner = this

        applyInsets(binding.registerParentLayout)

        binding.registerButton.setOnClickListener {
            view?.findNavController()
                ?.navigate(LoginFragmentDirections.actionLoginFragmentToNavigationHome())
        }

        return binding.root
    }
}