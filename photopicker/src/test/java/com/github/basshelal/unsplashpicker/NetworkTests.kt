package com.github.basshelal.unsplashpicker

import com.github.basshelal.unsplashpicker.data.UnsplashPhoto
import com.github.basshelal.unsplashpicker.network.NetworkEndpoints
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

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

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
        }
    }

    @DisplayName("Test")
    @Test
    fun testTest() {
        network
            .loadPhotos("", 1, 1)
            .subscribe(object : Observer<Response<List<UnsplashPhoto>>> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Response<List<UnsplashPhoto>>) {
                    println(t)
                }

                override fun onError(e: Throwable) {
                    error(e)
                }

            })
    }

}