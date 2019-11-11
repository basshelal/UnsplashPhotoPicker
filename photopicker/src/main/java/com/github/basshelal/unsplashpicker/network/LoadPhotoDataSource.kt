@file:SuppressLint("CheckResult")

package com.github.basshelal.unsplashpicker.network

import android.annotation.SuppressLint
import androidx.paging.PageKeyedDataSource
import com.github.basshelal.unsplashpicker.UnsplashPhotoPickerConfig
import com.github.basshelal.unsplashpicker.data.UnsplashPhoto
import com.github.basshelal.unsplashpicker.network.Repository.networkState
import retrofit2.Response

/**
 * Android paging library data source.
 * This will load the photos and allow an infinite scroll on the picker screen.
 */
internal class LoadPhotoDataSource(
        private val networkEndpoints: NetworkEndpoints
) : PageKeyedDataSource<Int, UnsplashPhoto>() {

    private var lastPage: Int? = null

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, UnsplashPhoto>) {
        networkState.postValue(NetworkState.LOADING)
        networkEndpoints.loadPhotos(
                UnsplashPhotoPickerConfig.accessKey, 1, params.requestedLoadSize
        ).subscribe(
                // onNext
                { response: Response<List<UnsplashPhoto>> ->
                    // if the response is successful
                    // we get the last page number
                    // we push the result on the paging callback
                    // we update the network state to success

                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        lastPage = response.headers()["x-total"]?.toInt()?.div(params.requestedLoadSize)

                        callback.onResult(body, null, 2)
                        networkState.postValue(NetworkState.SUCCESS)
                    }
                    // if the response is not successful
                    // we update the network state to error along with the error message
                    else {
                        networkState.postValue(NetworkState.error(response.message()))
                    }
                },
                // onError
                {
                    networkState.postValue(NetworkState.error(it.message))
                }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, UnsplashPhoto>) {
        networkState.postValue(NetworkState.LOADING)
        networkEndpoints.loadPhotos(
                UnsplashPhotoPickerConfig.accessKey, params.key, params.requestedLoadSize
        ).subscribe(
                // onNext
                { response: Response<List<UnsplashPhoto>> ->
                    val page = params.key
                    // if the response is successful
                    // we get the next page number
                    // we push the result on the paging callback
                    // we update the network state to success

                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        val nextPage = if (page == lastPage) null else page + 1
                        callback.onResult(body, nextPage)
                        networkState.postValue(NetworkState.SUCCESS)
                    }
                    // if the response is not successful
                    // we update the network state to error along with the error message
                    else {
                        networkState.postValue(NetworkState.error(response.message()))
                    }
                },
                // onError
                {
                    networkState.postValue(NetworkState.error(it.message))
                }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, UnsplashPhoto>) {
        // we do nothing here because everything will be loaded
    }
}
