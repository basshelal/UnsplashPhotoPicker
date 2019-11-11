package com.github.basshelal.unsplashpicker.network

import com.github.basshelal.unsplashpicker.data.UnsplashPhoto
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Retrofit endpoints definition.
 */
internal interface NetworkEndpoints {

    @GET("/photos")
    fun loadPhotos(
            @Query("client_id") clientId: String,
            @Query("page") page: Int,
            @Query("per_page") pageSize: Int
    ): Observable<Response<List<UnsplashPhoto>>>

    @GET("search/photos")
    fun searchPhotos(
            @Query("client_id") clientId: String,
            @Query("query") criteria: String,
            @Query("page") page: Int,
            @Query("per_page") pageSize: Int
    ): Observable<Response<SearchResponse>>

    @GET
    fun downloadPhoto(@Url url: String): Completable

    companion object {
        const val BASE_URL = "https://api.unsplash.com/"
    }
}
