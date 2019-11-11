package com.github.basshelal.unsplashpicker.network

import androidx.annotation.Keep

@Keep
internal enum class Status {
    LOADING,
    SUCCESS,
    FAILURE
}

internal enum class Error {
    NO_INTERNET,
    NO_RESULTS,
    SERVER_ERROR,
    CLIENT_ERROR
}

@Suppress("DataClassPrivateConstructor")
internal data class NetworkState private constructor(
        val status: Status,
        val msg: String? = null
) {
    companion object {
        val SUCCESS = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.LOADING)
        fun error(msg: String?) = NetworkState(Status.FAILURE, msg)
    }
}
