package com.github.basshelal.unsplashpicker.network

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.github.basshelal.unsplashpicker.data.UnsplashPhoto

/**
 * Android paging library data source factory.
 * This will create the load photo data source.
 */
internal class LoadPhotoDataSourceFactory(private val networkEndpoints: NetworkEndpoints) :
    DataSource.Factory<Int, UnsplashPhoto>() {

    val sourceLiveData = MutableLiveData<LoadPhotoDataSource>()

    override fun create(): DataSource<Int, UnsplashPhoto> {
        val source = LoadPhotoDataSource(networkEndpoints)
        sourceLiveData.postValue(source)
        return source
    }
}
