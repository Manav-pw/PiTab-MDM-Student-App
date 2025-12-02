package com.example.pitabmdmstudent.di

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.SharedPreferences
import com.example.pitabmdmstudent.BuildConfig
import com.example.pitabmdmstudent.data.auth.AuthPreferences
import com.example.pitabmdmstudent.data.remote.api.AuthApi
import com.example.pitabmdmstudent.data.remote.api.DeviceAuthApi
import com.example.pitabmdmstudent.data.remote.api.StudentApi
import com.example.pitabmdmstudent.data.remote.datasource.AuthDataSource
import com.example.pitabmdmstudent.data.remote.datasource.AuthDataSourceImpl
import com.example.pitabmdmstudent.data.remote.datasource.StudentDataSource
import com.example.pitabmdmstudent.data.remote.datasource.StudentDataSourceImpl
import com.example.pitabmdmstudent.data.remote.network.AuthInterceptor
import com.example.pitabmdmstudent.data.repository.DashboardRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        authPreferences: AuthPreferences,
    ): AuthInterceptor = AuthInterceptor(authPreferences)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        gson: Gson,
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.MDM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideStudentApi(retrofit: Retrofit): StudentApi {
        return retrofit.create(StudentApi::class.java)
    }

    @Provides
    @Singleton
    fun provideStudentDataSource(
        api: StudentApi
    ): StudentDataSource {
        return StudentDataSourceImpl(api)
    }

    @Provides
    @Singleton
    fun provideSharedPrefs(
        @ApplicationContext context: Context,
    ): SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideAuthPreferences(
        sharedPreferences: SharedPreferences,
    ): AuthPreferences = AuthPreferences(sharedPreferences)

    @Provides
    @Singleton
    fun provideUsageStatsManager(
        @ApplicationContext context: Context
    ): UsageStatsManager {
        return context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    @Provides
    @Singleton
    fun provideDashboardRepository(
        @ApplicationContext context: Context,
        usageStatsManager: UsageStatsManager
    ): DashboardRepository {
        return DashboardRepository(context)
    }

    @Provides
    @Singleton
    @Named("authRetrofit")
    fun provideAuthRetrofit(
        gson: Gson,
        client: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.AUTH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        @Named("authRetrofit") retrofit: Retrofit,
    ): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthDataSource(
        api: AuthApi,
    ): AuthDataSource = AuthDataSourceImpl(api)

    @Provides
    @Singleton
    fun provideDeviceAuthApi(
        retrofit: Retrofit,
    ): DeviceAuthApi {
        return retrofit.create(DeviceAuthApi::class.java)
    }

}