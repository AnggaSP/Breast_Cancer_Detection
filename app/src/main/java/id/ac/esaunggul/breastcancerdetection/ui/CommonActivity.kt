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

import android.animation.LayoutTransition
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.ActivityCommonBinding
import id.ac.esaunggul.breastcancerdetection.util.extensions.applyInsets
import id.ac.esaunggul.breastcancerdetection.util.extensions.removeInsets
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class CommonActivity : AppCompatActivity() {

    /*
     * Keep track of the authentication state as we need it to fend of the keyboard listener.
     */
    internal var hasLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        /*
         * Dynamically set system ui property for edge-to-edge experience in supported platform.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

        super.onCreate(savedInstanceState)

        val binding = ActivityCommonBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.lifecycleOwner = this

        /*
         * Adjust layout transition as the default one flickers on View.GONE.
         */
        val layoutTransition = LayoutTransition()
        layoutTransition.setDuration(LayoutTransition.APPEARING, 16)
        layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING)
        binding.commonParentLayout.layoutTransition = layoutTransition

        /*
         * Hide the navigation bar when keyboard pops up.
         * This normally isn't a problem but with adjustResize we need to do so.
         */
        KeyboardVisibilityEvent.setEventListener(
            this,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    if (isOpen) {
                        binding.commonNavView.visibility = View.GONE
                    } else if (hasLogin) {
                        binding.commonNavView.visibility = View.VISIBLE
                    }
                }
            }
        )

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.common_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_consultation, R.id.navigation_notification,
                R.id.navigation_diagnosis, R.id.navigation_profile
            )
        )
        binding.commonToolbar.setupWithNavController(navController, appBarConfiguration)
        binding.commonNavView.setupWithNavController(navController)

        /*
         * Prevent navigation component from recreating fragment on reselection.
         */
        binding.commonNavView.setOnNavigationItemReselectedListener {
            // Do nothing
        }

        /*
         * Hide the app bar and navigation bar when no user is authenticated.
         * Also hide the title as we use non-default title.
         */
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_auth -> {
                    removeInsets(binding.commonParentLayout)
                    hasLogin = false
                    binding.commonAppBar.visibility = View.GONE
                    binding.commonNavView.visibility = View.GONE
                    binding.commonToolbar.title = null
                }
                R.id.navigation_login -> {
                    removeInsets(binding.commonParentLayout)
                    hasLogin = false
                    binding.commonAppBar.visibility = View.GONE
                    binding.commonNavView.visibility = View.GONE
                    binding.commonToolbar.title = null
                }
                R.id.navigation_registration -> {
                    removeInsets(binding.commonParentLayout)
                    hasLogin = false
                    binding.commonAppBar.visibility = View.GONE
                    binding.commonNavView.visibility = View.GONE
                    binding.commonToolbar.title = null
                }
                else -> {
                    applyInsets(binding.commonParentLayout)
                    hasLogin = true
                    binding.commonAppBar.visibility = View.VISIBLE
                    binding.commonNavView.visibility = View.VISIBLE
                    binding.commonToolbar.title = null
                }
            }
        }
    }
}