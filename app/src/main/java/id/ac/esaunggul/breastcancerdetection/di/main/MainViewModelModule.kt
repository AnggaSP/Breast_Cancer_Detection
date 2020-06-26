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

package id.ac.esaunggul.breastcancerdetection.di.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import id.ac.esaunggul.breastcancerdetection.ui.main.user.UserViewModel
import id.ac.esaunggul.breastcancerdetection.ui.main.user.home.HomeArticleViewModel
import id.ac.esaunggul.breastcancerdetection.util.factory.MainViewModelFactory

@Module
abstract class MainViewModelModule {

    @MainScope
    @Binds
    abstract fun bindMainViewModelFactory(mainViewModelFactory: MainViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @MainViewModelKey(HomeArticleViewModel::class)
    abstract fun bindHomeArticleViewModel(homeArticleViewModel: HomeArticleViewModel): ViewModel

    @Binds
    @IntoMap
    @MainViewModelKey(UserViewModel::class)
    abstract fun bindUserViewModel(userViewModel: UserViewModel): ViewModel
}