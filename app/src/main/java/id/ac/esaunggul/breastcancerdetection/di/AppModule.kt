package id.ac.esaunggul.breastcancerdetection.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import id.ac.esaunggul.breastcancerdetection.data.api.DetectionApi
import id.ac.esaunggul.breastcancerdetection.data.repo.FirebaseRepo
import id.ac.esaunggul.breastcancerdetection.data.repo.Repo
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
abstract class AppModule {

    companion object {
        @Singleton
        @Provides
        fun provideAuthInstance(): FirebaseAuth = Firebase.auth

        @Singleton
        @Provides
        fun provideDatabaseInstance(): FirebaseFirestore = Firebase.firestore

        @Singleton
        @Provides
        fun provideRetrofitInstance(): DetectionApi {
            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .build()

            return Retrofit.Builder().baseUrl("https://api.fasilkom.me/")
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create()).build()
                .create(DetectionApi::class.java)
        }
    }

    @Singleton
    @Binds
    abstract fun bindRepo(repo: FirebaseRepo): Repo
}