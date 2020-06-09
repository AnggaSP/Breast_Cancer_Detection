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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.FragmentLoginBinding
import id.ac.esaunggul.breastcancerdetection.ui.common.BaseFragment

class LoginFragment : BaseFragment() {

    companion object {
        private const val TAG = "Login"
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
        val binding: FragmentLoginBinding by binds(
            R.layout.fragment_login,
            container
        )

        binding.lifecycleOwner = this

        applyInsets(binding.loginParentLayout)

        binding.loginButton.setOnClickListener {
            view?.findNavController()
                ?.navigate(LoginFragmentDirections.actionLoginFragmentToNavigationHome())
        }

        return binding.root
    }
}