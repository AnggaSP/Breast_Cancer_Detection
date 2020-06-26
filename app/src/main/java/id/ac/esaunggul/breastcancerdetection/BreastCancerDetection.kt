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

package id.ac.esaunggul.breastcancerdetection

import android.app.Application
import id.ac.esaunggul.breastcancerdetection.di.app.AppComponent
import id.ac.esaunggul.breastcancerdetection.di.app.DaggerAppComponent
import id.ac.esaunggul.breastcancerdetection.di.auth.AuthComponent
import id.ac.esaunggul.breastcancerdetection.di.main.MainComponent
import timber.log.Timber

class BreastCancerDetection : Application() {

    private val appComponent: AppComponent = DaggerAppComponent
        .factory()
        .create(this)

    private var authComponent: AuthComponent? = null
    private var mainComponent: MainComponent? = null

    fun authComponent(): AuthComponent {
        if (authComponent == null) {
            authComponent = appComponent.authComponent().create()
        }
        return authComponent as AuthComponent
    }

    fun releaseAuthComponent() {
        authComponent = null
    }

    fun mainComponent(): MainComponent {
        if (mainComponent == null) {
            mainComponent = appComponent.mainComponent().create()
        }
        return mainComponent as MainComponent
    }

    fun releaseMainComponent() {
        mainComponent = null
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}