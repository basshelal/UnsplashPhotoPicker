@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.unsplashpicker.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.github.basshelal.unsplashpicker.UnsplashPhotoPickerConfig
import com.github.basshelal.unsplashpicker.data.UnsplashPhoto
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

internal object Repository {

    private val networkEndpoints = createNetworkEndpoints()

    val networkState = MutableLiveData<NetworkState>()

    fun loadPhotos(pageSize: Int): Observable<PagedList<UnsplashPhoto>> {
        return RxPagedListBuilder(
                DataSourceFactory { LoadPhotoDataSource(networkEndpoints) },
                PagedList.Config.Builder()
                        .setInitialLoadSizeHint(pageSize)
                        .setPageSize(pageSize)
                        .build()
        ).buildObservable()
    }

    fun searchPhotos(criteria: String, pageSize: Int): Observable<PagedList<UnsplashPhoto>> {
        return RxPagedListBuilder(
                DataSourceFactory { SearchPhotoDataSource(networkEndpoints, criteria) },
                PagedList.Config.Builder()
                        .setInitialLoadSizeHint(pageSize)
                        .setPageSize(pageSize)
                        .build()
        ).buildObservable()
    }

    fun downloadPhoto(url: String?) {
        if (url != null) {
            val authUrl = "$url?client_id=${UnsplashPhotoPickerConfig.accessKey}"
            networkEndpoints.downloadPhoto(authUrl)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() { /* do nothing */
                        }

                        override fun onSubscribe(d: Disposable) {  /* do nothing */
                        }

                        override fun onError(e: Throwable) {
                            Log.e(Repository::class.java.simpleName, e.message, e)
                        }
                    })
        }
    }

    private inline fun createHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addNetworkInterceptor(
                    Interceptor { chain: Interceptor.Chain ->
                        chain.proceed(
                                chain.request().newBuilder()
                                        .addHeader("Content-Type", "application/json")
                                        .addHeader("Accept-Version", "v1")
                                        .build()
                        )
                    }
            )
            if (UnsplashPhotoPickerConfig.isLoggingEnabled) {
                addNetworkInterceptor(
                        HttpLoggingInterceptor().also {
                            it.level = HttpLoggingInterceptor.Level.BODY
                        }
                )
            }
            cache(Cache(UnsplashPhotoPickerConfig.application.cacheDir, 10L * 1024L * 1024L))
        }.build()
    }

    private inline fun createNetworkEndpoints(): NetworkEndpoints {
        return Retrofit.Builder()
                .baseUrl(NetworkEndpoints.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(createHttpClient())
                .build()
                .create(NetworkEndpoints::class.java)
    }
}
