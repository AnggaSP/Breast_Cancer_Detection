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

package id.ac.esaunggul.breastcancerdetection.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.ActivityCommonBinding
import id.ac.esaunggul.breastcancerdetection.ui.common.BaseActivity

class CommonActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        /*
         * Dynamically set system ui property for edge-to-edge experience in supported platform.
         */
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }

        super.onCreate(savedInstanceState)

        val binding: ActivityCommonBinding by binds(
            this,
            R.layout.activity_common
        )

        binding.lifecycleOwner = this

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.common_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.commonNavView.setupWithNavController(navController)

        /*
         * Hide the navigation bar when the user is not authenticated.
         */
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_auth -> binding.commonNavView.visibility = View.GONE
                R.id.navigation_login -> binding.commonNavView.visibility = View.GONE
                R.id.navigation_registration -> binding.commonNavView.visibility = View.GONE
                else -> binding.commonNavView.visibility = View.VISIBLE
            }
        }
    }
}