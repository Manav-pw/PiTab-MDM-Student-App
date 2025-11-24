package com.example.pitabmdmstudent.di

import android.app.Application
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.SharedPreferences
import com.example.pitabmdmstudent.BuildConfig
import com.example.pitabmdmstudent.data.api.StudentApi
import com.example.pitabmdmstudent.data.datasource.StudentDataSource
import com.example.pitabmdmstudent.data.datasource.StudentDataSourceImpl
import com.example.pitabmdmstudent.data.network.AuthInterceptor
import com.example.pitabmdmstudent.repository.DashboardRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
    fun provideAuthInterceptor(): AuthInterceptor = AuthInterceptor()

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
            .baseUrl(BuildConfig.PARENT_BASE_URL)
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
    fun provideSharedPrefs(app: Application): SharedPreferences =
        app.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

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
        return DashboardRepository(context, usageStatsManager)
    }

}