package com.example.newsapp.di.module

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.newsapp.data.api.ApiKeyInterceptor
import com.example.newsapp.data.api.NetworkService
import com.example.newsapp.data.local.AppDatabaseService
import com.example.newsapp.data.local.DatabaseService
import com.example.newsapp.data.local.NewsAppDatabase
import com.example.newsapp.di.BaseUrl
import com.example.newsapp.di.DatabaseName
import com.example.newsapp.di.NetworkAPIKey
import com.example.newsapp.utils.AppConstant
import com.example.newsapp.utils.DefaultDispatcherProvider
import com.example.newsapp.utils.DispatcherProvider
import com.example.newsapp.utils.NetworkHelper
import com.example.newsapp.utils.NetworkHelperImpl
import com.example.newsapp.utils.logger.AppLogger
import com.example.newsapp.utils.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    @Singleton
    fun provideNetworkService(
        @BaseUrl baseUrl: String,
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): NetworkService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(NetworkService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(apiKeyInterceptor: ApiKeyInterceptor):
        OkHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(apiKeyInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideNetworkHelper(@ApplicationContext context: Context): NetworkHelper {
        return NetworkHelperImpl(context)
    }

    @Provides
    @Singleton
    fun provideApiKeyInterceptor(@NetworkAPIKey apiKey: String): ApiKeyInterceptor =
        ApiKeyInterceptor(apiKey)

    @Provides
    @Singleton
    fun provideDispatcher(): DispatcherProvider = DefaultDispatcherProvider()

    @Provides
    @Singleton
    fun provideLogger(): Logger = AppLogger()

    @BaseUrl
    @Provides
    fun provideBaseUrl(): String = AppConstant.BASE_URL

    @NetworkAPIKey
    @Provides
    fun provideApiKey(): String = AppConstant.API_KEY

    @Provides
    @Singleton
    fun provideDatabaseService(appDatabase: NewsAppDatabase): DatabaseService {
        return AppDatabaseService(appDatabase)
    }

    @DatabaseName
    @Provides
    fun provideDatabaseName(): String = AppConstant.DATABASE_NAME

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        @DatabaseName databaseName: String,
    ): NewsAppDatabase {
        return Room.databaseBuilder(
            context,
            NewsAppDatabase::class.java,
            databaseName,
        ).build()
    }

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context,
    ): WorkManager {
        return WorkManager.getInstance(context)
    }
}
