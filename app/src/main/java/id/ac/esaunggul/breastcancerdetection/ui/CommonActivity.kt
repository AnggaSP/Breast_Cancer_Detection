package id.ac.esaunggul.breastcancerdetection.ui

import android.animation.LayoutTransition
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import id.ac.esaunggul.breastcancerdetection.R
import id.ac.esaunggul.breastcancerdetection.databinding.ActivityCommonBinding
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.MenuInflaterDispatcher
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.NavigationDispatcher
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.SharedPrefDispatcher
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.ToastDispatcher
import id.ac.esaunggul.breastcancerdetection.util.event.observe
import id.ac.esaunggul.breastcancerdetection.util.extensions.applyInsets
import id.ac.esaunggul.breastcancerdetection.util.extensions.removeInsets
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import javax.inject.Inject

@AndroidEntryPoint
class CommonActivity : AppCompatActivity() {

    @Inject
    lateinit var menuInflaterDispatcher: MenuInflaterDispatcher

    @Inject
    lateinit var navigationDispatcher: NavigationDispatcher

    @Inject
    lateinit var sharedPrefDispatcher: SharedPrefDispatcher

    @Inject
    lateinit var toastDispatcher: ToastDispatcher

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

        val sharedPref = getPreferences(Context.MODE_PRIVATE)

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

        /*
         * Dispatcher for menu inflater in auth viewmodel.
         */
        menuInflaterDispatcher.menuInflaterParam.observe(this) { param ->
            binding.commonNavView.menu.clear()
            binding.commonNavView.inflateMenu(param)
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.common_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.fragment_home,
                R.id.fragment_consultation,
                R.id.fragment_consultation_admin,
                R.id.fragment_notification,
                R.id.fragment_notification_admin,
                R.id.fragment_diagnosis,
                R.id.fragment_diagnosis_admin,
                R.id.fragment_profile,
                R.id.fragment_profile_admin
            )
        )
        binding.commonToolbar.setupWithNavController(navController, appBarConfiguration)
        binding.commonNavView.setupWithNavController(navController)

        /*
         * Dispatcher for navigation in viewmodel.
         */
        navigationDispatcher.navigationCommands.observe(this) { command ->
            command.invoke(navController)
        }

        sharedPrefDispatcher.sharedPrefCommands.observe(this) { command ->
            command.invoke(sharedPref)
        }

        /*
         * Dispatcher for toast in viewmodel.
         */
        toastDispatcher.toastParam.observe(this) { param ->
            Toast.makeText(this, param, Toast.LENGTH_LONG).show()
        }

        /*
         * Prevent navigation component from recreating fragment on reselection.
         */
        binding.commonNavView.setOnNavigationItemReselectedListener {
            // Do nothing
        }

        /*
         * Hide the app bar and navigation bar when no user is authenticated.
         */
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragment_auth -> {
                    removeInsets(binding.commonParentLayout)
                    hasLogin = false
                    binding.commonAppBar.visibility = View.GONE
                    binding.commonNavView.visibility = View.GONE
                }
                R.id.fragment_login -> {
                    removeInsets(binding.commonParentLayout)
                    hasLogin = false
                    binding.commonAppBar.visibility = View.GONE
                    binding.commonNavView.visibility = View.GONE
                }
                R.id.fragment_registration -> {
                    removeInsets(binding.commonParentLayout)
                    hasLogin = false
                    binding.commonAppBar.visibility = View.GONE
                    binding.commonNavView.visibility = View.GONE
                }
                else -> {
                    applyInsets(binding.commonParentLayout)
                    hasLogin = true
                    binding.commonAppBar.visibility = View.VISIBLE
                    binding.commonNavView.visibility = View.VISIBLE
                }
            }
        }
    }
}