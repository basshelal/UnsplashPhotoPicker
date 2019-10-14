package com.github.basshelal.unsplashpicker.domain

import android.util.Log
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.github.basshelal.unsplashpicker.UnsplashPhotoPickerConfig
import com.github.basshelal.unsplashpicker.data.NetworkEndpoints
import com.github.basshelal.unsplashpicker.data.UnsplashPhoto
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Simple repository used as a proxy by the view models to fetch data.
 */
internal class Repository constructor(private val networkEndpoints: NetworkEndpoints) {

    fun loadPhotos(pageSize: Int): Observable<PagedList<UnsplashPhoto>> {
        return RxPagedListBuilder(
            LoadPhotoDataSourceFactory(networkEndpoints),
            PagedList.Config.Builder()
                .setInitialLoadSizeHint(pageSize)
                .setPageSize(pageSize)
                .build()
        ).buildObservable()
    }

    fun searchPhotos(criteria: String, pageSize: Int): Observable<PagedList<UnsplashPhoto>> {
        return RxPagedListBuilder(
            SearchPhotoDataSourceFactory(networkEndpoints, criteria),
            PagedList.Config.Builder()
                .setInitialLoadSizeHint(pageSize)
                .setPageSize(pageSize)
                .build()
        ).buildObservable()
    }

    fun trackDownload(url: String?) {
        if (url != null) {
            val authUrl = url + "?client_id=" + UnsplashPhotoPickerConfig.accessKey
            networkEndpoints.trackDownload(authUrl)
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
}
