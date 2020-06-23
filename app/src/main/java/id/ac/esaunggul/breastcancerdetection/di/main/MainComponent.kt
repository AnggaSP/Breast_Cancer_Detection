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

import dagger.Subcomponent
import id.ac.esaunggul.breastcancerdetection.ui.main.user.consultation.ConsultationFragment
import id.ac.esaunggul.breastcancerdetection.ui.main.user.diagnosis.DiagnosisFormFragment
import id.ac.esaunggul.breastcancerdetection.ui.main.user.home.HomeArticleDetailFragment
import id.ac.esaunggul.breastcancerdetection.ui.main.user.home.HomeFragment
import id.ac.esaunggul.breastcancerdetection.ui.main.user.profile.ProfileFragment

@MainScope
@Subcomponent(
    modules = [
        MainModule::class,
        MainViewModelModule::class
    ]
)
interface MainComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainComponent
    }

    fun inject(articleDetailFragment: HomeArticleDetailFragment)
    fun inject(consultationFragment: ConsultationFragment)
    fun inject(diagnosisFormFragment: DiagnosisFormFragment)
    fun inject(homeFragment: HomeFragment)
    fun inject(profileFragment: ProfileFragment)
}