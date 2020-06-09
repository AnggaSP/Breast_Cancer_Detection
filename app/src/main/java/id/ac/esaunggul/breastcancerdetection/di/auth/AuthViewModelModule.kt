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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import id.ac.esaunggul.breastcancerdetection.ui.auth.AuthViewModel
import id.ac.esaunggul.breastcancerdetection.ui.auth.AuthViewModelFactory

@Module
abstract class AuthViewModelModule {

    @AuthScope
    @Binds
    abstract fun bindViewModelFactory(authViewModelFactory: AuthViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @AuthViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel
}