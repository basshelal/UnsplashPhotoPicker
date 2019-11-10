package com.github.basshelal.unsplashpicker

import com.github.basshelal.unsplashpicker.data.UnsplashPhoto
import com.github.basshelal.unsplashpicker.network.NetworkEndpoints
import com.github.basshelal.unsplashpicker.network.SearchResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
@DisplayName("Network Tests")
class NetworkTests {

    companion object {
        private fun createNetworkEndpoints(): NetworkEndpoints {
            return Retrofit.Builder()
                .baseUrl(NetworkEndpoints.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(
                    OkHttpClient.Builder().apply {
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
                    }.build()
                )
                .build()
                .create(NetworkEndpoints::class.java)
        }

        internal val network = createNetworkEndpoints()
    }

    @DisplayName("Load Single Photo")
    @Test
    fun testLoadSinglePhoto() {
        time("Load Single Photo") {
            network.loadPhotos(ACCESS_KEY, 1, 1)
                .subscribe(
                    { response: Response<List<UnsplashPhoto>> ->
                        assertTrue(response.isSuccessful)
                        assertEquals(200, response.code())
                        val list = response.body()
                        require(list != null)
                        assertTrue(list.isNotEmpty())
                        assertEquals(1, list.size)
                    },
                    {
                        error(it)
                    }
                )
        }
    }

    @DisplayName("Load Few Photos")
    @Test
    fun testLoadFewPhotos() {
        time("Load Few Photos") {
            network.loadPhotos(ACCESS_KEY, 1, 7)
                .subscribe(
                    { response: Response<List<UnsplashPhoto>> ->
                        assertTrue(response.isSuccessful)
                        assertEquals(200, response.code())
                        val list = response.body()
                        require(list != null)
                        assertTrue(list.isNotEmpty())
                        assertEquals(7, list.size)
                    },
                    {
                        error(it)
                    }
                )
        }
    }

    @DisplayName("Load Max Photos")
    @Test
    fun testLoadManyPhotos() {
        time("Load Max Photos") {
            network.loadPhotos(ACCESS_KEY, 1, 30)
                .subscribe(
                    { response: Response<List<UnsplashPhoto>> ->
                        assertTrue(response.isSuccessful)
                        assertEquals(200, response.code())
                        val list = response.body()
                        require(list != null)
                        assertTrue(list.isNotEmpty())
                        assertEquals(30, list.size)
                    },
                    {
                        error(it)
                    }
                )
        }
    }

    @DisplayName("Search Single Photo")
    @Test
    fun testSearchSinglePhoto() {
        time("Search Single Photo") {
            network.searchPhotos(ACCESS_KEY, "car", 1, 1)
                .subscribe(
                    { response: Response<SearchResponse> ->
                        assertTrue(response.isSuccessful)
                        assertEquals(200, response.code())

                        val body = response.body()
                        require(body != null)
                        assertTrue(body.total > 0)
                        assertTrue(body.total_pages > 0)

                        val list = body.results
                        assertTrue(list.isNotEmpty())
                        assertEquals(1, list.size)
                    },
                    {
                        error(it)
                    }
                )
        }
    }

    @DisplayName("Search Few Photos")
    @Test
    fun testSearchFewPhotos() {
        time("Search Few Photos") {
            network.searchPhotos(ACCESS_KEY, "car", 1, 7)
                .subscribe(
                    { response: Response<SearchResponse> ->
                        assertTrue(response.isSuccessful)
                        assertEquals(200, response.code())

                        val body = response.body()
                        require(body != null)
                        assertTrue(body.total > 0)
                        assertTrue(body.total_pages > 0)

                        val list = body.results
                        assertTrue(list.isNotEmpty())
                        assertEquals(7, list.size)
                    },
                    {
                        error(it)
                    }
                )
        }
    }

    @DisplayName("Search Max Photos")
    @Test
    fun testSearchMaxPhotos() {
        time("Search Max Photos") {
            network.searchPhotos(ACCESS_KEY, "car", 1, 30)
                .subscribe(
                    { response: Response<SearchResponse> ->
                        assertTrue(response.isSuccessful)
                        assertEquals(200, response.code())

                        val body = response.body()
                        require(body != null)
                        assertTrue(body.total > 0)
                        assertTrue(body.total_pages > 0)

                        val list = body.results
                        assertTrue(list.isNotEmpty())
                        assertEquals(30, list.size)
                    },
                    {
                        error(it)
                    }
                )
        }
    }

    @DisplayName("Download Photo")
    @Test
    fun testDownloadPhoto() {
        time("Download Photo") {
            network.searchPhotos(ACCESS_KEY, "car", 1, 1)
                .subscribe(
                    { response: Response<SearchResponse> ->
                        assertTrue(response.isSuccessful)
                        assertEquals(200, response.code())

                        val body = response.body()
                        require(body != null)
                        assertTrue(body.total > 0)
                        assertTrue(body.total_pages > 0)

                        val list = body.results
                        assertTrue(list.isNotEmpty())
                        assertEquals(1, list.size)

                        val photo = list.first()

                        var downloaded = false

                        photo.links.download_location?.also {
                            network.downloadPhoto(it + "?client_id=" + ACCESS_KEY)
                                .subscribe(
                                    {
                                        downloaded = true
                                    },
                                    {
                                        error(it)
                                    })
                        }
                        assertTrue(downloaded)
                    },
                    {
                        error(it)
                    }
                )
        }
    }

    private inline fun time(message: Any? = "", block: () -> Unit) {
        measureTimedValue(block).also {
            println("$message took ${it.duration}")
        }
    }
}