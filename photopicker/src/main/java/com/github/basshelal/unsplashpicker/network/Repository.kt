@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.unsplashpicker.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.github.basshelal.unsplashpicker.UnsplashPhotoPickerConfig
import com.github.basshelal.unsplashpicker.data.UnsplashPhoto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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

    val state = MutableLiveData<UnsplashPhotoPickerState>()

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
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        return Retrofit.Builder()
                .baseUrl(NetworkEndpoints.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(createHttpClient())
                .build()
                .create(NetworkEndpoints::class.java)
    }
}

enum class UnsplashPhotoPickerState {

    /*
    * Loading initial page (blank to non-blank) for search as well???
    * Loading new page (non-blank to non-blank but potential waiting on next page) for search as well???
    * No internet
    * No results
    * HTTP error server or client errors with codes
    * */

    LOADING,
    LOADED,
    NO_INTERNET,
    NO_RESULTS,
    ERROR;

    var message: String = ""
        private set
    var code: Int = 0
        private set

    companion object {
        fun ERROR(message: String) = ERROR.also { it.message = message }
    }
}

internal inline fun <K, V> DataSourceFactory(crossinline create: () -> DataSource<K, V>) =
        object : DataSource.Factory<K, V>() {
            override fun create(): DataSource<K, V> = create()
        }
