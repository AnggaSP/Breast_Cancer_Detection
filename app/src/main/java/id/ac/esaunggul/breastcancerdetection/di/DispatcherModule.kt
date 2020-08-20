package id.ac.esaunggul.breastcancerdetection.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.MenuInflaterDispatcher
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.NavigationDispatcher
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.SharedPrefDispatcher
import id.ac.esaunggul.breastcancerdetection.util.dispatcher.ToastDispatcher

@Module
@InstallIn(ActivityRetainedComponent::class)
object DispatcherModule {

    @ActivityRetainedScoped
    @Provides
    fun provideMenuInflater(): MenuInflaterDispatcher = MenuInflaterDispatcher()

    @ActivityRetainedScoped
    @Provides
    fun provideNavigation(): NavigationDispatcher = NavigationDispatcher()

    @ActivityRetainedScoped
    @Provides
    fun provideSharedPref(): SharedPrefDispatcher = SharedPrefDispatcher()

    @ActivityRetainedScoped
    @Provides
    fun provideToast(): ToastDispatcher = ToastDispatcher()
}