package com.github.basshelal.unsplashpicker

import com.github.basshelal.unsplashpicker.data.NetworkEndpoints
import com.github.basshelal.unsplashpicker.domain.Repository
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Manual dependency injection to avoid sticking to a specific dependency injection library.
 */
internal object Injector {

    val repository: Repository = Repository(createNetworkEndpoints())

    private fun createHeaderInterceptor() =
        Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept-Version", "v1")
                .build()
            chain.proceed(newRequest)
        }

    private fun createLoggingInterceptor() =
        HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }

    private fun createHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.addNetworkInterceptor(createHeaderInterceptor())
        if (UnsplashPhotoPickerConfig.isLoggingEnabled) {
            builder.addNetworkInterceptor(createLoggingInterceptor())
        }
        val cacheSize = 10 * 1024 * 1024 // 10 MB of cache
        val cache = Cache(UnsplashPhotoPickerConfig.application.cacheDir, cacheSize.toLong())
        builder.cache(cache)
        return builder.build()
    }

    private fun createRetrofitBuilder(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkEndpoints.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(createHttpClient())
            .build()
    }

    private fun createNetworkEndpoints(): NetworkEndpoints =
        createRetrofitBuilder().create(NetworkEndpoints::class.java)
}
