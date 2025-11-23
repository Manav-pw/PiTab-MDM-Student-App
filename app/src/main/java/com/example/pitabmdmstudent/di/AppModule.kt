package com.example.pitabmdmstudent.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSharedPrefs(app: Application): SharedPreferences =
        app.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
}