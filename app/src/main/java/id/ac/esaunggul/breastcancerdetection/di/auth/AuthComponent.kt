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

package id.ac.esaunggul.breastcancerdetection.di.auth

import dagger.Subcomponent
import id.ac.esaunggul.breastcancerdetection.ui.auth.AuthFragment
import id.ac.esaunggul.breastcancerdetection.ui.auth.login.LoginFragment
import id.ac.esaunggul.breastcancerdetection.ui.auth.register.RegisterFragment

@AuthScope
@Subcomponent(
    modules = [
        AuthModule::class,
        AuthViewModelModule::class
    ]
)
interface AuthComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): AuthComponent
    }

    fun inject(authFragment: AuthFragment)
    fun inject(loginFragment: LoginFragment)
    fun inject(registerFragment: RegisterFragment)
}