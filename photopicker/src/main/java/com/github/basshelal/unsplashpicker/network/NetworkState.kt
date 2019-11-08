package com.github.basshelal.unsplashpicker.network

/*
This has been copied from the Android paging library sample
 */

internal enum class Status {
    LOADING,
    SUCCESS,
    FAILURE
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
